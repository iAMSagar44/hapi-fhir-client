package dev.sagar.hapi_fhir_client.config;

public final class IdentifierSystem {

    public static final String MEDICARE =
            "http://ns.electronichealth.net.au/id/medicare-number";

    public static final String IHI =
            "http://ns.electronichealth.net.au/id/hi/ihi/1.0";

    private IdentifierSystem() {}

    public static String forIdentifier(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier must not be null");
        }
        if (identifier.matches("80036\\d{11}")) {
            return IHI;
        }
        if (identifier.matches("\\d{11}")) {
            return MEDICARE;
        }
        throw new IllegalArgumentException(
                "Invalid identifier: must be an 11-digit Medicare number or a 16-digit IHI starting with 80036");
    }
}
