package reservation_test;
import org.junit.jupiter.api.Test;
import reservation.model.Produit;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
public class ProduitTest {
    @Test
    void produitValide() {

        Produit produit = new Produit(
                "P01", "PC",
                new BigDecimal("10"),
                new BigDecimal("100")
        );

        assertEquals("P01", produit.getId());
        assertEquals("PC", produit.getLibelle());
        assertEquals(
                new BigDecimal("100.00"),
                produit.getCaution()
        );
    }

    @Test
    void tarifZeroRefuse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P01", "PC",
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void tarifNegatifRefuse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P01", "PC",
                        new BigDecimal("-10"),
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void cautionNegativeRefuse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P01", "PC",
                        BigDecimal.TEN,
                        new BigDecimal("-5")
                )
        );
    }

    @Test
    void cautionZeroAcceptee() {

        Produit produit = new Produit(
                "P01", "PC",
                BigDecimal.TEN,
                BigDecimal.ZERO
        );

        assertEquals(
                new BigDecimal("0.00"),
                produit.getCaution()
        );
    }

    @Test
    void identifiantVideRefuse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "", "PC",
                        BigDecimal.TEN,
                        BigDecimal.ZERO
                )
        );
    }

}
