package reservation.model;

import java.math.BigDecimal;

public class Produit {
    private String id;
    private String libelle;
    private BigDecimal tarifJournalier;
    private BigDecimal caution;

    public Produit(String id, String libelle, BigDecimal tarifJournalier, BigDecimal caution) {

        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Identifiant obligatoire");

        if (tarifJournalier == null || tarifJournalier.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Tarif journalier invalide");

        if (caution == null || caution.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Caution invalide");

        this.id = id;
        this.libelle = libelle;
        this.tarifJournalier = tarifJournalier;
        this.caution = caution.setScale(2,
                java.math.RoundingMode.HALF_UP);
    }

    public String getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public BigDecimal getTarifJournalier() {
        return tarifJournalier;
    }

    public BigDecimal getCaution() {
        return caution;
    }
}
