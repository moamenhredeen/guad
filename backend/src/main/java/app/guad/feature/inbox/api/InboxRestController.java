package app.guad.feature.inbox.api;

import app.guad.feature.inbox.InboxItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inbox")
class InboxRestController {

    @GetMapping
    public InboxItem[] list(){
        return new InboxItem[0];
    }
}
