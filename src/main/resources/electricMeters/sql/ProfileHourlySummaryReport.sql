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
         left join PROFILE_STRS t00 on t00.id = main.id and t00.time like '00:00-01:00'
         left join PROFILE_STRS t01 on t01.id = main.id and t01.time like '01:00-02:00'
         left join PROFILE_STRS t02 on t02.id = main.id and t02.time like '02:00-03:00'
         left join PROFILE_STRS t03 on t03.id = main.id and t03.time like '03:00-04:00'
         left join PROFILE_STRS t04 on t04.id = main.id and t04.time like '04:00-05:00'
         left join PROFILE_STRS t05 on t05.id = main.id and t05.time like '05:00-06:00'
         left join PROFILE_STRS t06 on t06.id = main.id and t06.time like '06:00-07:00'
         left join PROFILE_STRS t07 on t07.id = main.id and t07.time like '07:00-08:00'
         left join PROFILE_STRS t08 on t08.id = main.id and t08.time like '08:00-09:00'
         left join PROFILE_STRS t09 on t09.id = main.id and t09.time like '09:00-10:00'
         left join PROFILE_STRS t10 on t10.id = main.id and t10.time like '10:00-11:00'
         left join PROFILE_STRS t11 on t11.id = main.id and t11.time like '11:00-12:00'
         left join PROFILE_STRS t12 on t12.id = main.id and t12.time like '12:00-13:00'
         left join PROFILE_STRS t13 on t13.id = main.id and t13.time like '13:00-14:00'
         left join PROFILE_STRS t14 on t14.id = main.id and t14.time like '14:00-15:00'
         left join PROFILE_STRS t15 on t15.id = main.id and t15.time like '15:00-16:00'
         left join PROFILE_STRS t16 on t16.id = main.id and t16.time like '16:00-17:00'
         left join PROFILE_STRS t17 on t17.id = main.id and t17.time like '17:00-18:00'
         left join PROFILE_STRS t18 on t18.id = main.id and t18.time like '18:00-19:00'
         left join PROFILE_STRS t19 on t19.id = main.id and t19.time like '19:00-20:00'
         left join PROFILE_STRS t20 on t20.id = main.id and t20.time like '20:00-21:00'
         left join PROFILE_STRS t21 on t21.id = main.id and t21.time like '21:00-22:00'
         left join PROFILE_STRS t22 on t22.id = main.id and t22.time like '22:00-23:00'
         left join PROFILE_STRS t23 on t23.id = main.id and t23.time like '23:00-24:00'
WHERE main.PROFILE_ID in (1,5,3)
and cast(strftime('%m', main.DATE) as Integer) = ? -- Месяц
and cast(strftime('%Y', main.DATE) as Integer) = ? -- Год
group by main.date