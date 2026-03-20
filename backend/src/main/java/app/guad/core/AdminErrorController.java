package app.guad.core;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminErrorController implements ErrorController {

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public String handleAdminError(HttpServletRequest request, Model model) {
        var status = getStatus(request);
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());

        var message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (message != null && !message.toString().isEmpty()) {
            model.addAttribute("message", message.toString());
        }

        return switch (status.value()) {
            case 403 -> "admin/error/403";
            case 404 -> "admin/error/404";
            default -> "admin/error/error";
        };
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ApiError> handleApiError(HttpServletRequest request) {
        var status = getStatus(request);
        var message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        return ResponseEntity.status(status)
                .body(ApiError.of(status.value(), status.getReasonPhrase(),
                        message != null ? message.toString() : status.getReasonPhrase()));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        var statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode instanceof Integer code) {
            return HttpStatus.valueOf(code);
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
