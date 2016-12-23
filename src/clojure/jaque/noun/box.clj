(ns jaque.noun.box
  (:refer-clojure :exclude [atom])
  (:require [jaque.noun.core :refer [export]]))

; Because noun is a very powerful function that encompasses most/all of box's
; functionality and is used by other things in core, it's mostly inevitable
; that this file be subsumed by core. However you should still import from
; this namespace, not core.

(export atom cell map->nlr seq->it string->cord noun)
