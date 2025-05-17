package me.seungeun.repository;

import me.seungeun.Entity.VaccinationInfo;
import me.seungeun.Entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccine, Long> {

    // 특정 placeId에 해당하는 VaccinationInfo에 연결된 Vaccine 이름 리스트 조회
    @Query("SELECT v.vaccineName FROM Vaccine v WHERE v.vaccinationInfo.placeId = :placeId")
    List<String> findVaccinationTypesByPlaceId(@Param("placeId") String placeId);

}
