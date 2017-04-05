=+  ^=  answers
    :~  (add 2 2)  (add 4 4)  (add 255 16)  (add 0xffff 0xabcd)
        (add 0xffff.ffff.ffff.ffff 0xdead.beef.cede.deaf)
        %+  add
          0xffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff
        0xcade.edac.caac.daad.deed.ceed.cede.face.faac.fece
        :: 6
        (bex 1)  (bex 2)  (bex 31)  (bex 63)  (bex 256)  (bex 259)
        :: 12
        (can 5 ~[[1 1] [3 12]])  (can 0 ~[[5 31] [8 31.337]])
        %+  can  0
          :~  [65 0xabcd.dcba.dacb.cadb.badc.dcab.ffff.dead.feea]
              [32 0xcade.deed]
              [2 11]
              [18 0xabcd.deed.dead]
          ==
        :: 15
        (cap 2)  (cap 3)  (cap 7)  (cap 15)
        (cap 0xffff.ffff.ffff.ffff.ffff.ffff.ffff.abcd.dcba.feed)
        :: 20
        (cat 0 1 2)  (cat 1 0xff 0xff)  (cat 2 0xffff.ffff.ffff 0xff)
        %^  cat  4  0xabcd.dead.cade.aced.dace.feef
          0xdead.beef.feed.cade.aced.dddd
        :: 24
        (con 1 2)  (con 0xff00 0xff)
        %+  con  0xbeef.feed.cede.deaf
          0xdeaf.cede.feed.beef.fade
        :: 27
        (cut 0 [2 0] 4)  (cut 0 [3 2] 0xff)  (cut 3 [4 0] 0xabcd)
        %^  cut  6  [2 1]
           ::    one long     ::    two longs     ::       three      ::
          0xa0a1.b2b3.c4c5.d6d7.e8e9.f0f1.dead.beef.cade.fade.deaf.fece.face
        :: 31
        (dec 1)  (dec 43)  (dec 0xface.deaf.dead.beef)  
        (dec 0x1.0000.0000.0000.0000)
        :: 35
        (dis 1 2)  (dis 0xff00 0xff)
        %+  dis  0xbeef.feed.cede.deaf
          0xdeaf.cede.feed.beef.fade
        :: 38
        (div 4 2)  (div 16 4)  (div 17 4)  (div 64 31)  (div 0xffff 0xa)
        (div 0xcade.beef.feed.fede.fade.faad 0xaad.deaf)
        (div 0xcade.beef.feed.fede.fade.faad 0xfaad.deaf.beef)
        (div 0xcade.beef.feed.fede.fade.faad 0x1.faad.deaf.beef.abcd)
        :: 46
        (dvr 4 2)  (dvr 16 4)  (dvr 17 4)  (dvr 64 31)  (dvr 0xffff 0xa)
        (dvr 0xcade.beef.feed.fede.fade.faad 0xaad.deaf)
        (dvr 0xcade.beef.feed.fede.fade.faad 0xfaad.deaf.beef)
        (dvr 0xcade.beef.feed.fede.fade.faad 0x1.faad.deaf.beef.abcd)
        :: 53
    ==
    ::
::::::
::
=-
  %-  jam
  [7 gate-formula 9 2 [0 2] [1 answers] 0 7]
  ^=  gate-formula
  !=
  ::
::::
::
=>  0
=-
  =/  s/(list *)
    :~  (add 2 2)  (add 4 4)  (add 255 16)  (add 0xffff 0xabcd)
        (add 0xffff.ffff.ffff.ffff 0xdead.beef.cede.deaf)
        %+  add
          0xffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff.ffff
        0xcade.edac.caac.daad.deed.ceed.cede.face.faac.fece
        :: 6
        (bex 1)  (bex 2)  (bex 31)  (bex 63)  (bex 256)  (bex 259)
        :: 12
        (can 5 ~[[1 1] [3 12]])  (can 0 ~[[5 31] [8 31.337]])
        %+  can  0
          :~  [65 0xabcd.dcba.dacb.cadb.badc.dcab.ffff.dead.feea]
              [32 0xcade.deed]
              [2 11]
              [18 0xabcd.deed.dead]
          ==
        :: 15
        (cap 2)  (cap 3)  (cap 7)  (cap 15)
        (cap 0xffff.ffff.ffff.ffff.ffff.ffff.ffff.abcd.dcba.feed)
        :: 20
        (cat 0 1 2)  (cat 1 0xff 0xff)  (cat 2 0xffff.ffff.ffff 0xff)
        %^  cat  4  0xabcd.dead.cade.aced.dace.feef
          0xdead.beef.feed.cade.aced.dddd
        :: 24
        (con 1 2)  (con 0xff00 0xff)
        %+  con  0xbeef.feed.cede.deaf
          0xdeaf.cede.feed.beef.fade
        :: 27
        (cut 0 [2 0] 4)  (cut 0 [3 2] 0xff)  (cut 3 [4 0] 0xabcd)
        %^  cut  6  [2 1]
           ::    one long     ::    two longs     ::       three      ::
          0xa0a1.b2b3.c4c5.d6d7.e8e9.f0f1.dead.beef.cade.fade.deaf.fece.face
        :: 31
        (dec 1)  (dec 43)  (dec 0xface.deaf.dead.beef)  
        (dec 0x1.0000.0000.0000.0000)
        :: 35
        (dis 1 2)  (dis 0xff00 0xff)
        %+  dis  0xbeef.feed.cede.deaf
          0xdeaf.cede.feed.beef.fade
        :: 38
        (div 4 2)  (div 16 4)  (div 17 4)  (div 64 31)  (div 0xffff 0xa)
        (div 0xcade.beef.feed.fede.fade.faad 0xaad.deaf)
        (div 0xcade.beef.feed.fede.fade.faad 0xfaad.deaf.beef)
        (div 0xcade.beef.feed.fede.fade.faad 0x1.faad.deaf.beef.abcd)
        :: 46
        (dvr 4 2)  (dvr 16 4)  (dvr 17 4)  (dvr 64 31)  (dvr 0xffff 0xa)
        (dvr 0xcade.beef.feed.fede.fade.faad 0xaad.deaf)
        (dvr 0xcade.beef.feed.fede.fade.faad 0xfaad.deaf.beef)
        (dvr 0xcade.beef.feed.fede.fade.faad 0x1.faad.deaf.beef.abcd)
        :: 53
    ==
  |=  t/(list *)
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
  ~%    %mood
      +
    ~
  |%
  ++  bloq  @
  ++  list  |*  a/$-(* *)                                 ::  null-term list
            $@($~ {i/a t/(list a)})
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
  ::
  ++  bex                                                 ::  binary exponent
    ~/  %bex
    |=  a/@
    ^-  @
    ?:  =(0 a)  1
    (mul 2 $(a (dec a)))
  ::
  ++  can                                                 ::  assemble
    ~/  %can
    |=  {a/bloq b/(list {p/@u q/@})}
    ^-  @
    ?~  b  0
    (mix (end a p.i.b q.i.b) (lsh a p.i.b $(b t.b)))
  ::
  ++  cap                                                 ::  tree head
    ~/  %cap
    |=  a/@
    ^-  ?($2 $3)
    ?-  a
      $2        %2
      $3        %3
      ?($0 $1)  !!
      *         $(a (div a 2))
    ==
  ::
  ++  cat                                                 ::  concatenate
    ~/  %cat
    |=  {a/bloq b/@ c/@}
    (add (lsh a (met a b) c) b)
  ::
  ++  cut                                                 ::  slice
    ~/  %cut
    |=  {a/bloq {b/@u c/@u} d/@}
    (end a c (rsh a b d))
  ::
  ++  con                                                 ::  binary or
    ~/  %con
    |=  {a/@ b/@}
    =+  [c=0 d=0]
    |-  ^-  @
    ?:  ?&(=(0 a) =(0 b))  d
    %=  $
      a   (rsh 0 1 a)
      b   (rsh 0 1 b)
      c   +(c)
      d   %+  add  d
            %^  lsh  0  c
            ?&  =(0 (end 0 1 a))
                =(0 (end 0 1 b))
            ==
    ==
  ::
  ++  dec                                                 ::  decrement
    ~/  %dec
    |=  a/@
    ?<  =(0 a)
    =+  b=0
    |-  ^-  @
    ?:  =(a +(b))  b
    $(b +(b))
  ::

  ++  dis                                                 ::  binary and
    ~/  %dis
    |=  {a/@ b/@}
    =|  {c/@ d/@}
    |-  ^-  @
    ?:  ?|(=(0 a) =(0 b))  d
    %=  $
      a   (rsh 0 1 a)
      b   (rsh 0 1 b)
      c   +(c)
      d   %+  add  d
            %^  lsh  0  c
            ?|  =(0 (end 0 1 a))
                =(0 (end 0 1 b))
            ==
    ==
  ::
  ++  div                                                 ::  divide
    ~/  %div
    |:  [a=`@`1 b=`@`1]
    ^-  @
    ?<  =(0 b)
    =+  c=0
    |-
    ?:  (lth a b)  c
    $(a (sub a b), c +(c))
  ::
  ++  dvr
    ~/  %dvr
    |=  {a/@ b/@}  ^-  {p/@ q/@}
    ?<  =(0 b)
    [(div a b) (mod a b)]
  ::
  ++  end                                                 ::  tail
    ~/  %end
    |=  {a/bloq b/@u c/@}
    (mod c (bex (mul (bex a) b)))
  ::
  ++  lsh                                                 ::  left-shift
    ~/  %lsh
    |=  {a/bloq b/@u c/@}
    (mul (bex (mul (bex a) b)) c)
  ::
  ++  lth                                                 ::  less-than
    ~/  %lth
    |=  {a/@ b/@}
    ^-  ?
    ?&  !=(a b)
        |-
        ?|  =(0 a)
            ?&  !=(0 b)
                $(a (dec a), b (dec b))
    ==  ==  ==
  ::
  ++  mix                                                 ::  binary xor
    ~/  %mix
    |=  {a/@ b/@}
    ^-  @
    =+  [c=0 d=0]
    |-
    ?:  ?&(=(0 a) =(0 b))  d
    %=  $
      a   (rsh 0 1 a)
      b   (rsh 0 1 b)
      c   +(c)
      d   (add d (lsh 0 c =((end 0 1 a) (end 0 1 b))))
    ==
  ::
  ++  met                                                 ::  measure
    ~/  %met
    |=  {a/bloq b/@}
    ^-  @
    =+  c=0
    |-
    ?:  =(0 b)  c
    $(b (rsh a 1 b), c +(c))
  ::
  ++  mod                                                 ::  remainder
    ~/  %mod
    |:  [a=`@`1 b=`@`1]
    ^-  @
    ?<  =(0 b)
    (sub a (mul b (div a b)))
  ::
  ++  mul                                                 ::  multiply
    ~/  %mul
    |:  [a=`@`1 b=`@`1]
    ^-  @
    =+  c=0
    |-
    ?:  =(0 a)  c
    $(a (dec a), c (add b c))
  ::
  ++  rsh                                                 ::  right-shift
    ~/  %rsh
    |=  {a/bloq b/@u c/@}
    (div c (bex (mul (bex a) b)))
  ::
  ++  sub                                                 ::  subtract
    ~/  %sub
    |=  {a/@ b/@}
    ^-  @
    ?:  =(0 b)  a
    $(a (dec a), b (dec b))
  --
==
