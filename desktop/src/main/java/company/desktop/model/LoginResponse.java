package company.desktop.model;

public record LoginResponse(boolean success, String message, String token) {}
