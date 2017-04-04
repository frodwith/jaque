=+  ^=  answers
    :*  (add 2 2)  (add 4 4)  (add 255 16)  (add 0xffff 0xabcd)
        (add 0xffff.ffff.ffff.ffff 0xdead.beef.cede.deaf)
        %+  add
          0xffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff
        0xcade.edac.caac.daad.deed.ceed.cede.face.faac.fece
        :: 6
        (bex 1)  (bex 2)  (bex 31)  (bex 63)  (bex 256)  (bex 259)
        :: 12
        0
    ==
    ::
::::::
::
=-  [7 gate-formula 9 2 [0 2] [1 answers] 0 7]
  ^=  gate-formula
  !=  
  ::
::::
::
=>  0
=-
  =/  s/student
    :*  (add 2 2)  (add 4 4)  (add 255 16)  (add 0xffff 0xabcd)
        (add 0xffff.ffff.ffff.ffff 0xdead.beef.cede.deaf)
        %+  add
          0xffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff
        0xcade.edac.caac.daad.deed.ceed.cede.face.faac.fece
        :: 6
        (bex 1)  (bex 2)  (bex 31)  (bex 63)  (bex 256)  (bex 259)
        :: 12
        0
    ==
  |=  t/teacher
  =|  i/@
  |-
  ?~  s
    %.y
  ?~  t
    [%.n %length-mismatch]
  ?.  =(i.t i.s)
    [%.n i i.s i.t]
  $(t t.t, s t.s, i +(i))
  ::
::::
::
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
  ++  add                                                 ::  add
    ~/  %add
    |=  {a/@ b/@}
    ^-  @
    ?:  =(0 a)  b
    $(a (dec a), b +(b))
  ++  bex                                                 ::  binary exponent
    ~/  %bex
    |=  a/@
    ^-  @
    ?:  =(0 a)  1
    (mul 2 $(a (dec a)))
  ++  dec                                                 ::  decrement
    ~/  %dec
    |=  a/@
    ?<  =(0 a)
    =+  b=0
    |-  ^-  @
    ?:  =(a +(b))  b
    $(b +(b))
  ++  mul                                                 ::  multiply
    ~/  %mul
    |:  [a=`@`1 b=`@`1]
    ^-  @
    =+  c=0
    |-
    ?:  =(0 a)  c
    $(a (dec a), c (add b c))
  --
  |%
  ++  student  $@  $~  {i/@ t/student}
  ++  teacher  $@  $~  {i/@ t/teacher}
  --
==
