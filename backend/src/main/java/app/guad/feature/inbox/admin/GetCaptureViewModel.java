package app.guad.feature.inbox.admin;

import app.guad.feature.inbox.CaptureStatus;

record GetCaptureViewModel(
        Long id,
        String title,
        String description,
        CaptureStatus status
) {
}


