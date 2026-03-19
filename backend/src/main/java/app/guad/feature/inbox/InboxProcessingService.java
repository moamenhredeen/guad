package app.guad.feature.inbox;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.Action;
import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.context.Context;
import app.guad.feature.context.ContextRepository;
import app.guad.feature.document.Document;
import app.guad.feature.document.DocumentService;
import app.guad.feature.inbox.api.ProcessInboxItemRequest;
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
    private final ContextRepository contextRepository;

    public InboxProcessingService(InboxRepository inboxRepository, InboxService inboxService,
                                   ActionService actionService, ProjectService projectService,
                                   WaitingForService waitingForService, DocumentService documentService,
                                   ContextRepository contextRepository) {
        this.inboxRepository = inboxRepository;
        this.inboxService = inboxService;
        this.actionService = actionService;
        this.projectService = projectService;
        this.waitingForService = waitingForService;
        this.documentService = documentService;
        this.contextRepository = contextRepository;
    }

    @Transactional
    public Object process(Long inboxItemId, ProcessInboxItemRequest request, UUID userId) {
        var inboxItem = inboxRepository.findByIdAndUserId(inboxItemId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("InboxItem", inboxItemId));

        Object result = switch (request.action()) {
            case NEXT_ACTION -> {
                var action = new Action();
                action.setDescription(inboxItem.getTitle());
                action.setNotes(inboxItem.getDescription());
                action.setStatus(ActionStatus.NEXT);
                action.setUserId(userId);
                if (request.projectId() != null) {
                    projectService.getProjectById(request.projectId())
                        .ifPresent(action::setProject);
                }
                if (request.contextIds() != null && !request.contextIds().isEmpty()) {
                    var contexts = new HashSet<Context>();
                    for (var ctxId : request.contextIds()) {
                        contextRepository.findById(ctxId).ifPresent(contexts::add);
                    }
                    action.setContexts(contexts);
                }
                yield actionService.save(action);
            }
            case PROJECT -> {
                var project = new Project();
                project.setName(inboxItem.getTitle());
                project.setDescription(inboxItem.getDescription());
                project.setStatus(ProjectStatus.ACTIVE);
                project.setUserId(userId);
                yield projectService.save(project);
            }
            case WAITING_FOR -> {
                var wfi = new WaitingForItem();
                wfi.setTitle(inboxItem.getTitle());
                wfi.setNotes(inboxItem.getDescription());
                wfi.setDelegatedTo(request.delegatedTo());
                wfi.setStatus(WaitingForItemStatus.WAITING);
                wfi.setUserId(userId);
                if (request.projectId() != null) {
                    projectService.getProjectById(request.projectId())
                        .ifPresent(wfi::setProject);
                }
                yield waitingForService.save(wfi);
            }
            case SOMEDAY_MAYBE -> {
                var action = new Action();
                action.setDescription(inboxItem.getTitle());
                action.setNotes(inboxItem.getDescription());
                action.setStatus(ActionStatus.SOMEDAY_MAYBE);
                action.setUserId(userId);
                yield actionService.save(action);
            }
            case REFERENCE -> {
                var doc = new Document();
                doc.setName(inboxItem.getTitle());
                doc.setContent(inboxItem.getDescription());
                yield documentService.save(doc);
            }
            case TRASH -> null;
        };

        // Mark inbox item as processed
        inboxItem.setStatus(InboxItemStatus.PROCESSED);
        inboxItem.setProcessedDate(Instant.now());
        inboxService.save(inboxItem);

        return result;
    }
}
