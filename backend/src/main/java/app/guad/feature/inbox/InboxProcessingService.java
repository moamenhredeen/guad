package app.guad.feature.inbox;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.Action;
import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.area.AreaService;
import app.guad.feature.context.Context;
import app.guad.feature.context.ContextService;
import app.guad.feature.document.Document;
import app.guad.feature.document.DocumentService;
import app.guad.feature.inbox.api.ProcessCaptureRequest;
import app.guad.feature.project.Project;
import app.guad.feature.project.ProjectService;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.waitingfor.WaitingForItem;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

@Service
public class InboxProcessingService {

    private final InboxRepository inboxRepository;
    private final InboxService inboxService;
    private final ActionService actionService;
    private final ProjectService projectService;
    private final WaitingForService waitingForService;
    private final DocumentService documentService;
    private final ContextService contextService;
    private final AreaService areaService;

    public InboxProcessingService(InboxRepository inboxRepository, InboxService inboxService,
                                   ActionService actionService, ProjectService projectService,
                                   WaitingForService waitingForService, DocumentService documentService,
                                   ContextService contextService, AreaService areaService) {
        this.inboxRepository = inboxRepository;
        this.inboxService = inboxService;
        this.actionService = actionService;
        this.projectService = projectService;
        this.waitingForService = waitingForService;
        this.documentService = documentService;
        this.contextService = contextService;
        this.areaService = areaService;
    }

    @Transactional
    @Deprecated
    public Object process(Long captureId, ProcessCaptureRequest request, UUID userId) {
        var capture = inboxRepository.findById(captureId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Capture", captureId));

        Object result = switch (request.action()) {
            case NEXT_ACTION -> {
                var action = new Action();
                action.setDescription(firstPresent(request.description(), capture.getTitle()));
                action.setNotes(firstPresent(request.notes(), capture.getDescription()));
                action.setStatus(ActionStatus.NEXT);
                action.setEnergyLevel(request.energyLevel());
                action.setEstimatedDuration(request.estimatedDuration());
                action.setDueDate(request.dueDate());
                action.setScheduledDate(request.scheduledDate());
                action.setUserId(userId);
                if (request.projectId() != null) {
                    projectService.findById(request.projectId())
                        .ifPresent(action::setProject);
                }
                if (request.areaId() != null) {
                    areaService.getAreaById(request.areaId())
                        .ifPresent(action::setArea);
                }
                if (request.contextIds() != null && !request.contextIds().isEmpty()) {
                    var contexts = new HashSet<Context>();
                    for (var ctxId : request.contextIds()) {
                        contextService.findById(ctxId).ifPresent(contexts::add);
                    }
                    action.setContexts(contexts);
                }
                yield actionService.save(action);
            }
            case PROJECT -> {
                var project = new Project();
                project.setName(capture.getTitle());
                project.setDescription(capture.getDescription());
                project.setStatus(ProjectStatus.ACTIVE);
                project.setUserId(userId);
                yield projectService.save(project);
            }
            case WAITING_FOR -> {
                var wfi = new WaitingForItem();
                wfi.setTitle(capture.getTitle());
                wfi.setNotes(capture.getDescription());
                wfi.setDelegatedTo(request.delegatedTo());
                wfi.setStatus(WaitingForItemStatus.WAITING);
                wfi.setUserId(userId);
                if (request.projectId() != null) {
                    projectService.findById(request.projectId())
                        .ifPresent(wfi::setProject);
                }
                yield waitingForService.save(wfi);
            }
            case SOMEDAY_MAYBE -> {
                var action = new Action();
                action.setDescription(capture.getTitle());
                action.setNotes(capture.getDescription());
                action.setStatus(ActionStatus.SOMEDAY_MAYBE);
                action.setUserId(userId);
                yield actionService.save(action);
            }
            case REFERENCE -> {
                var doc = new Document();
                doc.setName(capture.getTitle());
                doc.setContent(capture.getDescription());
                yield documentService.save(doc);
            }
            case TRASH -> null;
        };

        // Mark inbox item as processed
        capture.setStatus(CaptureStatus.PROCESSED);
        capture.setProcessedDate(Instant.now());
        inboxService.save(capture);

        return result;
    }

    private String firstPresent(String candidate, String fallback) {
        return candidate != null && !candidate.isBlank() ? candidate : fallback;
    }
}
