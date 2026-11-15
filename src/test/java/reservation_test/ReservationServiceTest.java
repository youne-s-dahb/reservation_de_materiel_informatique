package reservation_test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservation.model.Commande;
import reservation.model.Etudiant;
import reservation.model.Produit;
import reservation.service.ReservationService;
import reservation.exception.ReservationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class ReservationServiceTest {
    private ReservationService service;

    @BeforeEach
    void setUp() {
        service = new ReservationService();
    }
    @Test
    void Test_identifiantEtudiantVide() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "",
                        "P001",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
        assertEquals(
                "Identifiant étudiant obligatoire",
                exception.getMessage()
        );
    }
    @Test
    void Test_identifiantProduitVide() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001",
                        "",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
        assertEquals(
                "Identifiant Produit obligatoire",
                exception.getMessage()
        );
    }
    @Test
    void Test_dateDebutAbsente() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001",
                        "P001",
                        null,
                        LocalDate.now()
                )
        );

        assertEquals(
                "Date début obligatoire",
                exception.getMessage()
        );
    }
    @Test
    void Test_dateFinAbsente() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now(),
                        null
                )
        );

        assertEquals(
                "Date fin obligatoire",
                exception.getMessage()
        );
    }

    @Test
    void Test_dateDebutDansLePasse() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().minusDays(1),
                        LocalDate.now()
                )
        );

        assertEquals(
                "La date de début ne peut pas être dans le passé",
                exception.getMessage()
        );
    }
    @Test
    void Test_dateFinAvantDateDebut() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(2)
                )
        );

        assertEquals(
                "La date de fin ne peut pas être avant la date de début",
                exception.getMessage()
        );
    }
    @Test
    void Test_dateDebutEgaleDateFin() {
        assertDoesNotThrow(
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
    }
    @Test
    void T08_etudiantInexistant() {
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E999",
                        "P001",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );

        assertEquals("Étudiant introuvable", exception.getMessage());
    }
    @Test
    void T09_produitInexistant() {
        // Préparer un étudiant existant
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001",
                        "P999",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );

        assertEquals(
                "Produit introuvable",
                exception.getMessage()
        );
    }
    @Test
    void T10_dureeReservationUnJour() {
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("100.00"),
                        new BigDecimal("500.00")
                )
        );

        assertDoesNotThrow(
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(1)
                )
        );
    }
    @Test
    void T11_dureeReservationSeptJours() {
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("100.00"),
                        new BigDecimal("500.00")
                )
        );

        assertDoesNotThrow(
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(7)
                )
        );
    }
    @Test
    void T12_dureeReservationHuitJours() {
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("100.00"),
                        new BigDecimal("500.00")
                )
        );

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(8)
                )
        );

        assertEquals(
                "La durée maximale de réservation est de 7 jours",
                exception.getMessage()
        );
    }
    @Test
    void T13_etudiantSansReservationActive() {
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("100.00"),
                        new BigDecimal("500.00")
                )
        );

        assertDoesNotThrow(
                () -> service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                )
        );
    }
    @Test
    void T14_etudiantAvecUneReservationActive() {
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("100.00"),
                        new BigDecimal("500.00")
                )
        );

        service.ajouterProduit(
                new Produit(
                        "P002",
                        "Clavier",
                        new BigDecimal("20.00"),
                        new BigDecimal("50.00")
                )
        );

        // Première réservation active
        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );

        // Deuxième réservation active : elle doit être acceptée
        assertDoesNotThrow(
                () -> service.reserver(
                        "E001",
                        "P002",
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(4)
                )
        );
    }
    @Test
    void T15_etudiantAvecDeuxReservationsActives() {
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit("P001", "Ordinateur",
                        new BigDecimal("100.00"), new BigDecimal("500.00"))
        );

        service.ajouterProduit(
                new Produit("P002", "Clavier",
                        new BigDecimal("20.00"), new BigDecimal("50.00"))
        );

        service.ajouterProduit(
                new Produit("P003", "Souris",
                        new BigDecimal("10.00"), new BigDecimal("20.00"))
        );

        // Première réservation active
        service.reserver(
                "E001", "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );

        // Deuxième réservation active
        service.reserver(
                "E001", "P002",
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(4)
        );

        // La troisième réservation doit être refusée
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E001", "P003",
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(6)
                )
        );

        assertEquals(
                "L'étudiant a atteint le maximum de 2 réservations actives",
                exception.getMessage()
        );
    }
    @Test
    void T16_reservationTermineeNonComptee() {

        // Ajouter un étudiant et un produit
        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit("P001", "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200"))
        );

        // Créer une réservation terminée
        Commande ancienneCommande = new Commande(
                "CMD001",
                "E001",
                "P001",
                LocalDate.now().minusDays(5),
                LocalDate.now().minusDays(3),
                3,
                new BigDecimal("300"),
                new BigDecimal("200"),
                new BigDecimal("500")
        );

        service.ajouterCommande(ancienneCommande);

        // Ajouter une réservation active
        service.reserver(
                "E001",
                "P001",
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        // Cette troisième réservation doit être acceptée
        assertDoesNotThrow(() ->
                service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(4)
                )
        );
    }

    @Test
    void T17_produitDisponible() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200")
                )
        );

        assertDoesNotThrow(() ->
                service.reserver(
                        "E001",
                        "P001",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3)
                )
        );
    }
    @Test
    void T18_reservationAvecPeriodeChevauchante() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterEtudiant(
                new Etudiant("E002", "Ahmed", "Ali")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200")
                )
        );

        // Première réservation
        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        // Deuxième réservation : période chevauchante
        assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E002",
                        "P001",
                        LocalDate.now().plusDays(4),
                        LocalDate.now().plusDays(7)
                )
        );
    }
    @Test
    void T19_reservationAvecDateLimiteCommune() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterEtudiant(
                new Etudiant("E002", "Ahmed", "Ali")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200")
                )
        );

        // Première réservation
        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        // Deuxième réservation : même date de fin et de début
        assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E002",
                        "P001",
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(7)
                )
        );
    }
    @Test
    void T20_deuxProduitsDifferentsMemePeriode() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterEtudiant(
                new Etudiant("E002", "Ahmed", "Ali")
        );

        service.ajouterProduit(
                new Produit("P001", "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200"))
        );

        service.ajouterProduit(
                new Produit("P002", "Souris",
                        new BigDecimal("20"),
                        new BigDecimal("50"))
        );

        // Première réservation : produit P001
        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4)
        );

        // Deuxième réservation : produit P002, même période
        assertDoesNotThrow(() ->
                service.reserver(
                        "E002",
                        "P002",
                        LocalDate.now().plusDays(2),
                        LocalDate.now().plusDays(4)
                )
        );
    }
    @Test
    void T21_tarifJournalierEgalZero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("0"),
                        new BigDecimal("200")
                )
        );
    }
    @Test
    void T22_tarifJournalierNegatif() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("-100"),
                        new BigDecimal("200")
                )
        );
    }
    @Test
    void T23_cautionEgaleZeroAcceptee() {

        assertDoesNotThrow(
                () -> new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("0")
                )
        );
    }
    @Test
    void T24_cautionNegativeRefusee() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("-50")
                )
        );
    }
    @Test
    void T25_calculMontantLocation() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200")
                )
        );

        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        // Durée inclusive : 3 jours
        BigDecimal montantAttendu = new BigDecimal("300.00");

        BigDecimal montantObtenu = service.getCommandes()
                .get(0)
                .getMontantLocation();

        assertEquals(0, montantAttendu.compareTo(montantObtenu));
    }
    @Test
    void T26_calculMontantTotal() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200")
                )
        );

        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        Commande commande = service.getCommandes().get(0);

        BigDecimal montantAttendu = new BigDecimal("500.00");

        assertEquals(
                0,
                montantAttendu.compareTo(commande.getMontantTotal())
        );
    }
    @Test
    void T27_arrondiMontantLocationHALF_UP() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("10.555"),
                        new BigDecimal("0")
                )
        );

        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1)
        );

        Commande commande = service.getCommandes().get(0);

        BigDecimal montantAttendu = new BigDecimal("10.56");

        assertEquals(
                0,
                montantAttendu.compareTo(commande.getMontantLocation())
        );
    }
    @Test
    void T28_reservationValideEnregistree() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200")
                )
        );

        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        // Vérifier qu'une commande a été enregistrée
        assertEquals(1, service.getCommandes().size());

        // Vérifier les informations de la commande
        Commande commande = service.getCommandes().get(0);

        assertEquals("E001", commande.getIdEtudiant());
        assertEquals("P001", commande.getIdProduit());
        assertEquals(3, commande.getDuree());
    }
    @Test
    void T29_referencesCommandesUniques() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterProduit(
                new Produit("P001", "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200"))
        );

        service.ajouterProduit(
                new Produit("P002", "Souris",
                        new BigDecimal("20"),
                        new BigDecimal("50"))
        );

        // Première réservation
        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );

        // Deuxième réservation avec un autre produit
        service.reserver(
                "E001",
                "P002",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );

        // Récupérer les deux commandes
        Commande commande1 = service.getCommandes().get(0);
        Commande commande2 = service.getCommandes().get(1);

        // Vérifier que les références sont différentes
        assertNotEquals(
                commande1.getReference(),
                commande2.getReference()
        );
    }
    @Test
    void T30_reservationRefuseeNeModifiePasCommandes() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Younes", "Ed-Dahbi")
        );

        service.ajouterEtudiant(
                new Etudiant("E002", "Ahmed", "Ali")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "PC",
                        new BigDecimal("100"),
                        new BigDecimal("200")
                )
        );

        // Première réservation acceptée
        service.reserver(
                "E001",
                "P001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        // Mémoriser le nombre de commandes avant le refus
        int nombreAvant = service.getCommandes().size();

        // Deuxième réservation refusée : chevauchement
        assertThrows(
                ReservationException.class,
                () -> service.reserver(
                        "E002",
                        "P001",
                        LocalDate.now().plusDays(2),
                        LocalDate.now().plusDays(4)
                )
        );

        // Vérifier que le nombre de commandes n'a pas changé
        int nombreApres = service.getCommandes().size();

        assertEquals(nombreAvant, nombreApres);
    }
}
