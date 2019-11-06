package at.searles.meelan

enum class BaseTypes: Type {
    Integer {
        override fun convert(node: Node): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Real {
        override fun byteCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Cplx {
        override fun convert(node: Node): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Bool {
        override fun convert(node: Node): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Unit {
        override fun convert(node: Node): Node? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}