package at.searles.meelan

enum class BaseTypes: Type {
    Integer {
        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Real {
        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Cplx {
        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Bool {
        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Unit {
        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, String {
        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}