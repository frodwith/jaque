package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.Node;

import net.frodwith.jaque.truffle.Types;

@TypeSystemReference(Types.class)
public abstract class JaqueNode extends Node {
}
