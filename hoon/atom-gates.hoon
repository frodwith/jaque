=+  ^=  answers
    :*  (add 2 2)
        (add 4 4)
        0
    ==
=-  [7 subject-formula 9 2 [0 2] [1 answers] 0 7]
  ^=  subject-formula
  !=  
=~
  %main
  ~%    %main
      ~
    ~
  |%
  ++  main  %main
  --
  ~%    %lib
      +
    ~
  |%
  ++  dec                                                 ::  decrement
    ~/  %dec
    |=  a/@
    ?<  =(0 a)
    =+  b=0
    |-  ^-  @
    ?:  =(a +(b))  b
    $(b +(b))
  ++  add                                                 ::  add
    ~/  %add
    |=  {a/@ b/@}
    ^-  @
    ?:  =(0 a)  b
    $(a (dec a), b +(b))
  --
  |%
  ++  student  $@  $~  {i/{label/@ value/@} t/student}
  ++  teacher  $@  $~  {i/@ t/teacher}
  --
  =/  s/student
    :*  :-  %1  (add 2 2)
        :-  %2  (add 4 4)
        0
    ==
  |=  t/teacher
  ?~  s
    %.y
  ?~  t
    [%.n %length-mismatch]
  ?.  =(i.t value.i.s)
    [%.n label.i.s]
  $(t t.t, s t.s)
==
