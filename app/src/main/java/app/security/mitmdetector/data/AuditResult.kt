package app.security.mitmdetector.data

sealed class AuditResult {
    data class VulnerabilityDetected(val message: String) : AuditResult()
    data class Error(val message: String) : AuditResult()
    object NoAlert : AuditResult()
}