--1.
INSERT INTO Periods
SELECT DISTINCT day, per
FROM Assigned
ORDER BY day, per;

--2.
INSERT INTO Teachers
SELECT tname, SUM(nper) AS tload
FROM Courses AS C, Taught_By AS T
WHERE C.cno = T.cno
GROUP BY tname
ORDER BY tname;

--3.
SELECT day, per, cno
FROM Assigned NATURAL JOIN Taught_By
WHERE tname = 'Marsalis'
ORDER BY day, per;

--4.
UPDATE Assigned
SET per = 4
WHERE cno = 10 AND day = 'B' AND per = 2;

--5.
SELECT day, per, cno
FROM Assigned NATURAL JOIN Taught_To
WHERE grade = 11 AND (hr = 'B' OR hr = 'Z')
ORDER BY day, per;

--6.
UPDATE Assigned
SET per = 2
WHERE per = 4 AND day = 'B'
AND cno =
(SELECT cno FROM Courses NATURAL JOIN Taught_To NATURAL JOIN Assigned
WHERE day = 'B' AND per = 4 AND grade = 11 AND subj = 'Mat');

--7.
SELECT tname, day, per, subj, grade, hr
FROM Courses NATURAL JOIN Taught_By
NATURAL JOIN Taught_To
NATURAL JOIN Assigned
ORDER BY tname, day, per;

--8.
SELECT grade, hr, day, per, subj
FROM Assigned NATURAL JOIN
Courses NATURAL JOIN
(SELECT cno, G.grade, G.hr
FROM Grades AS G, Taught_To AS T
WHERE G.grade = T.grade AND
(G.hr = T.hr
OR T.hr = 'Z')) AS foo
ORDER BY grade, hr, day, per;

--9.
SELECT tname, day, per
FROM taught_by NATURAL JOIN assigned
EXCEPT ALL
SELECT DISTINCT tname, day, per
FROM taught_by NATURAL JOIN assigned;

--10.
SELECT grade, hr, day, per
FROM Assigned NATURAL JOIN
(SELECT cno,G.grade, G.hr
FROM Grades AS G, Taught_To AS T
WHERE G.grade=T.grade AND (G.hr=T.hr OR T.hr='Z')
) AS foo1
EXCEPT ALL
SELECT DISTINCT grade, hr, day, per
FROM Assigned NATURAL JOIN
(SELECT cno,G.grade, G.hr
FROM Grades AS G, Taught_To AS T
WHERE G.grade=T.grade AND (G.hr=T.hr OR T.hr='Z')
) AS foo2
ORDER BY grade, hr, day, per;

--11.
SELECT A.cno, A.subj, A.day, A.per
FROM (SELECT * FROM Assigned NATURAL JOIN Courses) AS A,
Assigned AS B
WHERE A.cno = B.cno AND
A.day = B.day AND
A.per <> B.per;

--12.
UPDATE Assigned
SET per = 2
WHERE cno = 14 AND day = 'A' AND per = 4;

--13.
UPDATE Assigned
SET day = 'D'
WHERE cno = 3 AND day = 'B' AND per = 2;

--14.
UPDATE Assigned
SET per = 4
WHERE cno = 19 AND day = 'B' AND per = 2;

--15.
UPDATE Assigned
SET per = 4
WHERE cno = 22 AND day = 'A' AND per = 2;

--16.
UPDATE Assigned
SET day = 'D', per = 1
WHERE cno = 4 AND day = 'C' AND per = 2;

--17.
SELECT cno, nper - count AS rest
FROM Courses NATURAL JOIN
(SELECT cno, COUNT(*) AS count FROM Assigned
GROUP BY cno) AS foo
WHERE nper - count > 0
ORDER BY cno;

--18.
SELECT DISTINCT tname, subj
FROM Courses NATURAL JOIN Taught_By
WHERE tname NOT IN
(SELECT A.tname FROM
(SELECT tname, subj
FROM Courses NATURAL JOIN Taught_By) AS A,
(SELECT tname, subj
FROM Courses NATURAL JOIN Taught_By) AS B
WHERE A.tname = B.tname AND
A.subj <> B.subj);

--19.
SELECT * INTO Temp1
FROM Assigned NATURAL JOIN Taught_By
ORDER BY tname, day, per;

SELECT tname, day ,COUNT(per) AS sum, MIN(per) AS min, MAX(per) AS max
INTO Temp2
FROM Temp1
GROUP BY tname, day
HAVING COUNT(per) = 2 OR COUNT(per) = 3
ORDER BY tname;

DELETE FROM Temp2
WHERE sum = 3 AND max - min <> 3;

SELECT tname, day, (sum - 2) AS windows
INTO Temp3
FROM Temp2
WHERE sum = 3;

SELECT tname, day, (max - min - 1) AS windows
INTO Temp4
FROM Temp2
WHERE sum = 2;

DELETE FROM Temp4
WHERE windows = 0;

INSERT INTO Temp4
(SELECT * FROM Temp3);

SELECT * INTO
Resultnineteen
FROM Temp4
ORDER BY tname, day, windows;

DROP TABLE Temp1;
DROP TABLE Temp2;
DROP TABLE Temp3;
DROP TABLE Temp4;

SELECT * FROM Resultnineteen;

--20.
SELECT tname, SUM(windows) AS sum
INTO Resulttwenty
FROM Resultnineteen
GROUP BY tname
ORDER BY tname;

SELECT A.tname INTO Temp1
FROM Teachers AS A
WHERE A.tname NOT IN (SELECT tname FROM Resulttwenty);

INSERT INTO Resulttwenty(SELECT A.tname, 0 FROM Temp1 AS A);

DROP TABLE Temp1;

SELECT * FROM Resulttwenty;

--21.
SELECT SUM(sum)
AS total
INTO Resulttwentyone
FROM Resulttwenty;

SELECT * FROM Resulttwentyone;

--22.
SELECT cno, tname, day, per
INTO A
FROM Assigned NATURAL JOIN Taught_By;

SELECT cno, tname, day, per
INTO B
FROM Assigned NATURAL JOIN Taught_By;

SELECT DISTINCT A.cno, B.day, B.per
INTO Resulttwentytwo
FROM A, B
WHERE A.tname = B.tname AND A.cno <> B.cno;

DROP TABLE A;
DROP TABLE B;

SELECT * FROM Resulttwentytwo;

--23.
SELECT cno, G.grade, G.hr
INTO Temp1
FROM Grades AS G, Taught_To AS T
WHERE G.grade = T.grade AND
(G.hr = T.hr OR T.hr = 'Z');

SELECT * INTO A
FROM Temp1 NATURAL JOIN Assigned;

SELECT * INTO B
FROM Temp1 NATURAL JOIN Assigned;

SELECT DISTINCT A.cno, B.day, B.per
INTO Resulttwentythree
FROM A, B
WHERE A.grade = B.grade AND
A.hr = B.hr AND
A.cno <> B.cno;

DROP TABLE A;
DROP TABLE B;
DROP TABLE Temp1;

SELECT * FROM Resulttwentythree;

--24.
SELECT tname, day, COUNT(per) AS numper
INTO A
FROM Assigned NATURAL JOIN Taught_By
GROUP BY tname, day;

SELECT tname, MAX(numper) AS maxper
INTO B
FROM A
GROUP BY tname;

SELECT A.tname, A.day, B.maxper
INTO Resulttwentyfour
FROM A NATURAL JOIN B
WHERE A.numper = B.maxper;

DROP TABLE A;
DROP TABLE B;

SELECT * FROM Resulttwentyfour;