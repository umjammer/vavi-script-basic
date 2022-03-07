100 for i = 1 to 10 : print i : next i
110 dim b(20)
120 for i = 1 to 20 : b(i) = rnd(1) * 100 : next i
130 for i = 1 to 20
140 if b(i) < 50 then goto 160
150 print "low ";
155 goto 165
160 print "high ";
165 print b(i)
170 next i
