
package reservation.service;

import reservation.exception.ReservationException;
import reservation.model.Etudiant;
import reservation.model.Produit;
import reservation.model.Commande;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationService {

    private final List<Etudiant> etudiants = new ArrayList<>();
    private final List<Produit> produits = new ArrayList<>();
    private final List<Commande> commandes = new ArrayList<>();

    public void ajouterEtudiant(Etudiant etudiant) {
        etudiants.add(etudiant);
    }

    public void ajouterProduit(Produit produit) {
        produits.add(produit);
    }
    public void ajouterCommande(Commande commande) {
        commandes.add(commande);
    }
    public List<Commande> getCommandes() {
        return List.copyOf(commandes);
    }
    public void reserver(
            String idEtudiant,
            String idProduit,
            LocalDate dateDebut,
            LocalDate dateFin) {

        // T01 : Vérifier l'identifiant étudiant
        if (idEtudiant == null || idEtudiant.isBlank()) {
            throw new ReservationException(
                    "Identifiant étudiant obligatoire");
        }

        // T02 : Vérifier l'identifiant produit
        if (idProduit == null || idProduit.isBlank()) {
            throw new ReservationException(
                    "Identifiant Produit obligatoire");
        }

        // T03 : Vérifier la date de début
        if (dateDebut == null) {
            throw new ReservationException(
                    "Date début obligatoire");
        }

        // T04 : Vérifier la date de fin
        if (dateFin == null) {
            throw new ReservationException(
                    "Date fin obligatoire");
        }

        // T05 : Date de début dans le passé
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new ReservationException(
                    "La date de début ne peut pas être dans le passé");
        }

        // T06 : Date de fin avant date de début
        if (dateFin.isBefore(dateDebut)) {
            throw new ReservationException(
                    "La date de fin ne peut pas être avant la date de début");
        }

        // T08 : Vérifier l'existence de l'étudiant
        boolean etudiantExiste = etudiants.stream()
                .anyMatch(e -> e.getId().equals(idEtudiant));

        if (!etudiantExiste) {
            throw new ReservationException("Étudiant introuvable");
        }

        // T09 : Vérifier l'existence du produit
        Produit produit = produits.stream()
                .filter(p -> p.getId().equals(idProduit))
                .findFirst()
                .orElseThrow(() ->
                        new ReservationException("Produit introuvable"));

        // T10, T11, T12 : Calculer et vérifier la durée
        long duree = ChronoUnit.DAYS.between(
                dateDebut, dateFin) + 1;

        if (duree > 7) {
            throw new ReservationException(
                    "La durée maximale de réservation est de 7 jours");
        }

        // T13, T14, T15 : Vérifier le quota de réservations actives
        long reservationsActives = commandes.stream()
                .filter(c -> c.getIdEtudiant().equals(idEtudiant))
                .filter(c -> !c.getDateFin().isBefore(LocalDate.now()))
                .count();

        if (reservationsActives >= 2) {
            throw new ReservationException(
                    "L'étudiant a atteint le maximum de 2 réservations actives");
        }
        // T18 : Vérifier si le produit est déjà réservé pendant cette période
        boolean produitIndisponible = commandes.stream()
                .filter(c -> c.getIdProduit().equals(idProduit))
                .anyMatch(c ->
                        !dateDebut.isAfter(c.getDateFin())
                                && !dateFin.isBefore(c.getDateDebut())
                );

        if (produitIndisponible) {
            throw new ReservationException(
                    "Ce produit est déjà réservé pendant cette période"
            );
        }



        // Calculer le montant de location
        BigDecimal montantLocation = produit.getTarifJournalier()
                .multiply(BigDecimal.valueOf(duree))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        // Récupérer la caution
        BigDecimal caution = produit.getCaution();

        // Calculer le montant total
        BigDecimal montantTotal = montantLocation.add(caution);

        // Créer la commande
        Commande commande = new Commande(
                UUID.randomUUID().toString(),
                idEtudiant,
                idProduit,
                dateDebut,
                dateFin,
                duree,
                montantLocation,
                caution,
                montantTotal
        );

        // Enregistrer la commande après toutes les validations
        commandes.add(commande);
    }
}
