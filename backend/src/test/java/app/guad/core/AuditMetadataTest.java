package app.guad.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuditMetadataTest {
    @Test
    void newInstance_hasNullFields() {
        var audit = new AuditMetadata();
        assertNull(audit.getCreatedAt());
        assertNull(audit.getUpdatedAt());
        assertNull(audit.getCreatedBy());
        assertNull(audit.getUpdatedBy());
    }
}
