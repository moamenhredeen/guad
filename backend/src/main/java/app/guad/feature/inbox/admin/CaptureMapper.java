package app.guad.feature.inbox.admin;

import app.guad.feature.inbox.Capture;

final class CaptureMapper {
    private CaptureMapper(){}

    public static GetCaptureViewModel toGetCaptureViewModel(Capture capture) {
        return new GetCaptureViewModel(
                capture.getId(),
                capture.getTitle(),
                capture.getDescription(),
                capture.getStatus()
        );
    }

    public static CaptureDetailsViewModel toCaptureDetailsViewModel(Capture capture) {
        return new CaptureDetailsViewModel(
                capture.getId(),
                capture.getTitle(),
                capture.getDescription(),
                capture.getStatus(),
                capture.getAudit().getCreatedAt(),
                capture.getAudit().getUpdatedAt(),
                capture.getProcessedDate(),
                null
        );
    }

    public static DeleteCaptureViewModel toDeleteCaptureViewModel(Capture capture) {
        return new DeleteCaptureViewModel(capture.getId(), capture.getTitle());
    }
}

