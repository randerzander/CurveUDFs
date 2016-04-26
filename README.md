A series of Apache Hive UDFs for performing:

Dynamic Time Warping

Polynomial Spline Interpolation

Trilateration

Built for Hive 1.2.1.

Comparing a series of signals with FastDTW:
```
add jar /home/dev/CurveUDFs/target/CurveUDFs-0.1.0-SNAPSHOT.jar;
create temporary function fastdtw as 'com.github.randerzander.CurveUDFs.UDFFastDTW';
select 
  collect_set(concat_ws('|', a.mnemonic, b.mnemonic, cast(fastdtw(a.x, a.y, b.x, b.y, 10) as string))) as mnemonics
from (
    select file_no, step_type, mnemonic, uom, collect_list(step) as x, collect_list(reading) as y
    from log_readings
    group by file_no, step_type, mnemonic, uom) a
join (
    select file_no, step_type, mnemonic, uom, collect_list(step) as x, collect_list(reading) as y
    from log_readings
    group by file_no, step_type, mnemonic, uom) b
  on a.step_type = b.step_type and a.uom = b.uom and a.file_no = b.file_no
where a.mnemonic != b.mnemonic and fastdtw(a.x, a.y, b.x, b.y, 10) < 2000
;
```
