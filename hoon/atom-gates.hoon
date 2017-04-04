=+  ^=  answers
    :*  (add 2 2)  (add 4 4)  (add 255 16)  (add 0xffff 0xabcd)
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
  =/  s/(list @)
    :*  (add 2 2)  (add 4 4)  (add 255 16)  (add 0xffff 0xabcd)
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
        0
    ==
  |=  t/(list @)
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
  ++  dec                                                 ::  decrement
    ~/  %dec
    |=  a/@
    ?<  =(0 a)
    =+  b=0
    |-  ^-  @
    ?:  =(a +(b))  b
    $(b +(b))
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
