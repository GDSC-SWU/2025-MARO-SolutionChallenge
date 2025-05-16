package com.example.maro.model

object VaccineSchedule {
    val schedule: List<Triple<String, IntRange, String>> = listOf(
       //HepB 1,2,3
        Triple("HepB", 0..0, "HepB_1"),
        Triple("HepB", 1..1, "HepB_2"),
        Triple("HepB", 6..6, "HepB_3"),

       //BCG
        Triple("BCG", 0..1, "BCG_1"),

        // DTaP (2, 4, 6, 15~18 month, 4~6 age)
        Triple("DTaP", 2..2, "DTaP_1"),
        Triple("DTaP", 4..4, "DTaP_2"),
        Triple("DTaP", 6..6, "DTaP_3"),
        Triple("DTaP", 15..18, "DTaP_4"),
        Triple("DTaP", 48..72, "DTaP_5"),

        // IPV (2, 4, 6~18 month, 4~6 age)
        Triple("IPV", 2..2, "IPV_1"),
        Triple("IPV", 4..4, "IPV_2"),
        Triple("IPV", 6..18, "IPV_3"),
        Triple("IPV", 48..72, "IPV_4"),

        // Hib (2, 4, 6, 12~15 month)
        Triple("Hib", 2..2, "Hib_1"),
        Triple("Hib", 4..4, "Hib_2"),
        Triple("Hib", 6..6, "Hib_3"),
        Triple("Hib", 12..15, "Hib_4"),

        // PCV (2, 4, 6, 12~15 month)
        Triple("PCV", 2..2, "PCV_1"),
        Triple("PCV", 4..4, "PCV_2"),
        Triple("PCV", 6..6, "PCV_3"),
        Triple("PCV", 12..15, "PCV_4"),

        // MMR (12~15 month, 4~6 age)
        Triple("MMR", 12..15, "MMR_1"),
        Triple("MMR", 48..72, "MMR_2"),

        // VAR
        Triple("VAR", 12..15, "VAR_1"),

        // HepA (12~23month, +6month later 2차)
        Triple("HepA", 12..23, "HepA_1"),
        Triple("HepA", 18..36, "HepA_2"),

        //IJEV  ( 1~5)
        Triple("IJEV", 12..13, "IJEV_1"),
        Triple("IJEV", 24..25, "IJEV_2"),
        Triple("IJEV", 72..72, "IJEV_3"),
        Triple("IJEV", 120..120, "IJEV_4"),
        Triple("IJEV", 144..144, "IJEV_5"),

        //LJEV
        Triple("LJEV", 12..12, "LJEV_1"),
        Triple("LJEV", 24..24, "LJEV_2"),

        //HPV (only girl, 11~12 age)
        Triple("HPV", 132..144, "HPV_1"),

        //IIV (6month ~ 9 age까지 매년 권장)
        Triple("IIV", 6..108, "IIV")
    )
}
