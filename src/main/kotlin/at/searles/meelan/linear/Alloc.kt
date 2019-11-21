package at.searles.meelan.linear

import at.searles.meelan.Type

class Alloc(val id: String, val type: Type): CodeLine {
	// purely a marker to properly handle var assignments.
}
