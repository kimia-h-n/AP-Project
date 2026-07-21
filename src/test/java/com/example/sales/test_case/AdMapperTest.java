package com.example.sales.test_case;

import com.example.sales.ad.fav.FavoriteAd;
import com.example.sales.ad.model.Ad;
import com.example.sales.ad.model.AdCardSummary;
import com.example.sales.ad.model.AdMapper;
import com.example.sales.province.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdMapperTest {

    private AdMapper adMapper;

    @BeforeEach
    void setUp() {
        adMapper = Mappers.getMapper(AdMapper.class);
    }

    @Test
    void shouldMapAdToCartSummaryIncludingPersianCityName() {
        // Arrange
        City city = new City();
        city.setLabel("TEHRAN");
        city.setName("تهران");

        Instant createdAt =
                Instant.parse("2026-07-01T10:00:00Z");

        Instant updatedAt =
                Instant.parse("2026-07-02T12:00:00Z");

        Ad ad = new Ad();
        ad.setId(10L);
        ad.setTitle("Test advertisement");
        ad.setPrice(2_000_000L);
        ad.setCreatedAt(createdAt);
        ad.setUpdatedAt(updatedAt);
        ad.setCity(city);

        // Act
        AdCardSummary result =
                adMapper.toCartSummery(ad);

        // Assert
        assertNotNull(result);

        assertAll(
                () -> assertEquals(
                        10L,
                        result.getId()
                ),
                () -> assertEquals(
                        "Test advertisement",
                        result.getTitle()
                ),
                () -> assertEquals(
                        2_000_000L,
                        result.getPrice()
                ),
                () -> assertEquals(
                        createdAt,
                        result.getCreatedAt()
                ),
                () -> assertEquals(
                        updatedAt,
                        result.getUpdatedAt()
                ),
                () -> assertEquals(
                        "تهران",
                        result.getCityName(),
                        "city.name must be mapped to cityName"
                ),
                () -> assertNotEquals(
                        "TEHRAN",
                        result.getCityName(),
                        "city.label must not be mapped to cityName"
                )
        );
    }

    @Test
    void shouldMapListOfAdsToCartSummaries() {
        // Arrange
        City tehran = new City();
        tehran.setLabel("TEHRAN");
        tehran.setName("تهران");

        City shiraz = new City();
        shiraz.setLabel("SHIRAZ");
        shiraz.setName("شیراز");

        Ad firstAd = new Ad();
        firstAd.setId(1L);
        firstAd.setTitle("First ad");
        firstAd.setPrice(1000L);
        firstAd.setCity(tehran);

        Ad secondAd = new Ad();
        secondAd.setId(2L);
        secondAd.setTitle("Second ad");
        secondAd.setPrice(2000L);
        secondAd.setCity(shiraz);

        List<Ad> ads = List.of(firstAd, secondAd);

        // Act
        List<AdCardSummary> result =
                adMapper.toCartSummeryList(ads);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertAll(
                () -> assertEquals(
                        1L,
                        result.get(0).getId()
                ),
                () -> assertEquals(
                        "تهران",
                        result.get(0).getCityName()
                ),
                () -> assertNotEquals(
                        "TEHRAN",
                        result.get(0).getCityName()
                ),
                () -> assertEquals(
                        2L,
                        result.get(1).getId()
                ),
                () -> assertEquals(
                        "شیراز",
                        result.get(1).getCityName()
                ),
                () -> assertNotEquals(
                        "SHIRAZ",
                        result.get(1).getCityName()
                )
        );
    }

    @Test
    void shouldMapFavoriteAdUsingAdIdNotFavoriteId() {
        // Arrange
        City city = new City();
        city.setLabel("TABRIZ");
        city.setName("تبریز");

        Ad ad = new Ad();
        ad.setId(100L);
        ad.setTitle("Favorite advertisement");
        ad.setPrice(5_000_000L);
        ad.setCity(city);

        FavoriteAd favorite = new FavoriteAd();

        // شناسه Favorite عمداً با شناسه Ad متفاوت است
        favorite.setId(999L);
        favorite.setAd(ad);

        // Act
        AdCardSummary result =
                adMapper.toCartSummeryFromFavorite(favorite);

        // Assert
        assertNotNull(result);

        assertAll(
                () -> assertEquals(
                        100L,
                        result.getId(),
                        "Summary ID must be taken from ad.id, not favorite.id"
                ),
                () -> assertNotEquals(
                        999L,
                        result.getId(),
                        "Favorite ID must not be used as the advertisement ID"
                ),
                () -> assertEquals(
                        "Favorite advertisement",
                        result.getTitle()
                ),
                () -> assertEquals(
                        5_000_000L,
                        result.getPrice()
                ),
                () -> assertEquals(
                        "تبریز",
                        result.getCityName(),
                        "ad.city.name must be mapped to cityName"
                ),
                () -> assertNotEquals(
                        "TABRIZ",
                        result.getCityName(),
                        "ad.city.label must not be mapped to cityName"
                )
        );
    }

    @Test
    void shouldMapListOfFavoritesUsingNestedAdProperties() {
        // Arrange
        City mashhad = new City();
        mashhad.setLabel("MASHHAD");
        mashhad.setName("مشهد");

        City isfahan = new City();
        isfahan.setLabel("ISFAHAN");
        isfahan.setName("اصفهان");

        Ad firstAd = new Ad();
        firstAd.setId(10L);
        firstAd.setTitle("First favorite");
        firstAd.setPrice(100_000L);
        firstAd.setCity(mashhad);

        Ad secondAd = new Ad();
        secondAd.setId(20L);
        secondAd.setTitle("Second favorite");
        secondAd.setPrice(200_000L);
        secondAd.setCity(isfahan);

        FavoriteAd firstFavorite = new FavoriteAd();
        firstFavorite.setId(1000L);
        firstFavorite.setAd(firstAd);

        FavoriteAd secondFavorite = new FavoriteAd();
        secondFavorite.setId(2000L);
        secondFavorite.setAd(secondAd);

        List<FavoriteAd> favorites =
                List.of(firstFavorite, secondFavorite);

        // Act
        List<AdCardSummary> result =
                adMapper.toCartSummeryFromFavorites(favorites);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        AdCardSummary firstSummary = result.get(0);
        AdCardSummary secondSummary = result.get(1);

        assertAll(
                () -> assertEquals(
                        10L,
                        firstSummary.getId()
                ),
                () -> assertEquals(
                        "مشهد",
                        firstSummary.getCityName()
                ),
                () -> assertNotEquals(
                        "MASHHAD",
                        firstSummary.getCityName()
                ),
                () -> assertEquals(
                        20L,
                        secondSummary.getId()
                ),
                () -> assertEquals(
                        "اصفهان",
                        secondSummary.getCityName()
                ),
                () -> assertNotEquals(
                        "ISFAHAN",
                        secondSummary.getCityName()
                )
        );
    }

    @Test
    void shouldIgnoreImageFieldsDuringAdMapping() {
        // Arrange
        City city = new City();
        city.setLabel("KARAJ");
        city.setName("کرج");

        Ad ad = new Ad();
        ad.setId(40L);
        ad.setTitle("Advertisement without mapped image");
        ad.setPrice(10_000L);
        ad.setCity(city);

        // Act
        AdCardSummary result =
                adMapper.toCartSummery(ad);

        // Assert
        assertNotNull(result);

        assertAll(
                () -> assertEquals(
                        "کرج",
                        result.getCityName()
                ),
                () -> assertNotEquals(
                        "KARAJ",
                        result.getCityName()
                ),
                () -> assertNull(
                        result.getPrimaryImageId(),
                        "Mapper must ignore primaryImageId"
                ),
                () -> assertNull(
                        result.getPrimaryImageUrl(),
                        "Mapper must ignore primaryImageUrl"
                )
        );
    }

    @Test
    void shouldIgnoreImageFieldsDuringFavoriteMapping() {
        // Arrange
        City city = new City();
        city.setLabel("QOM");
        city.setName("قم");

        Ad ad = new Ad();
        ad.setId(50L);
        ad.setTitle("Favorite without mapped image");
        ad.setPrice(20_000L);
        ad.setCity(city);

        FavoriteAd favorite = new FavoriteAd();
        favorite.setId(500L);
        favorite.setAd(ad);

        // Act
        AdCardSummary result =
                adMapper.toCartSummeryFromFavorite(favorite);

        // Assert
        assertNotNull(result);

        assertAll(
                () -> assertEquals(
                        50L,
                        result.getId()
                ),
                () -> assertEquals(
                        "قم",
                        result.getCityName()
                ),
                () -> assertNotEquals(
                        "QOM",
                        result.getCityName()
                ),
                () -> assertNull(
                        result.getPrimaryImageId(),
                        "Mapper must ignore primaryImageId"
                ),
                () -> assertNull(
                        result.getPrimaryImageUrl(),
                        "Mapper must ignore primaryImageUrl"
                )
        );
    }

    @Test
    void shouldReturnNullWhenAdIsNull() {
        // Act
        AdCardSummary result =
                adMapper.toCartSummery(null);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldKeepCityNameNullWhenAdCityIsNull() {
        // Arrange
        Ad ad = new Ad();
        ad.setId(70L);
        ad.setTitle("Advertisement without city");
        ad.setPrice(3000L);
        ad.setCity(null);

        // Act
        AdCardSummary result =
                adMapper.toCartSummery(ad);

        // Assert
        assertNotNull(result);
        assertNull(result.getCityName());
    }
}
