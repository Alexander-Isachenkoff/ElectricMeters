SELECT main.date,
                sum(t00.aPos) T_0,
                sum(t01.aPos) T_1,
                sum(t02.aPos) T_2,
                sum(t03.aPos) T_3,
                sum(t04.aPos) T_4,
                sum(t05.aPos) T_5,
                sum(t06.aPos) T_6,
                sum(t07.aPos) T_7,
                sum(t08.aPos) T_8,
                sum(t09.aPos) T_9,
                sum(t10.aPos) T_10,
                sum(t11.aPos) T_11,
                sum(t12.aPos) T_12,
                sum(t13.aPos) T_13,
                sum(t14.aPos) T_14,
                sum(t15.aPos) T_15,
                sum(t16.aPos) T_16,
                sum(t17.aPos) T_17,
                sum(t18.aPos) T_18,
                sum(t19.aPos) T_19,
                sum(t20.aPos) T_20,
                sum(t21.aPos) T_21,
                sum(t22.aPos) T_22,
                sum(t23.aPos) T_23
FROM PROFILE_STRS main
         left join PROFILE_STRS t00 on t00.id = main.id and t00.HOUR = 0
         left join PROFILE_STRS t01 on t01.id = main.id and t01.HOUR = 1
         left join PROFILE_STRS t02 on t02.id = main.id and t02.HOUR = 2
         left join PROFILE_STRS t03 on t03.id = main.id and t03.HOUR = 3
         left join PROFILE_STRS t04 on t04.id = main.id and t04.HOUR = 4
         left join PROFILE_STRS t05 on t05.id = main.id and t05.HOUR = 5
         left join PROFILE_STRS t06 on t06.id = main.id and t06.HOUR = 6
         left join PROFILE_STRS t07 on t07.id = main.id and t07.HOUR = 7
         left join PROFILE_STRS t08 on t08.id = main.id and t08.HOUR = 8
         left join PROFILE_STRS t09 on t09.id = main.id and t09.HOUR = 9
         left join PROFILE_STRS t10 on t10.id = main.id and t10.HOUR = 10
         left join PROFILE_STRS t11 on t11.id = main.id and t11.HOUR = 11
         left join PROFILE_STRS t12 on t12.id = main.id and t12.HOUR = 12
         left join PROFILE_STRS t13 on t13.id = main.id and t13.HOUR = 13
         left join PROFILE_STRS t14 on t14.id = main.id and t14.HOUR = 14
         left join PROFILE_STRS t15 on t15.id = main.id and t15.HOUR = 15
         left join PROFILE_STRS t16 on t16.id = main.id and t16.HOUR = 16
         left join PROFILE_STRS t17 on t17.id = main.id and t17.HOUR = 17
         left join PROFILE_STRS t18 on t18.id = main.id and t18.HOUR = 18
         left join PROFILE_STRS t19 on t19.id = main.id and t19.HOUR = 19
         left join PROFILE_STRS t20 on t20.id = main.id and t20.HOUR = 20
         left join PROFILE_STRS t21 on t21.id = main.id and t21.HOUR = 21
         left join PROFILE_STRS t22 on t22.id = main.id and t22.HOUR = 22
         left join PROFILE_STRS t23 on t23.id = main.id and t23.HOUR = 23
WHERE main.PROFILE_ID = ?
group by main.date
