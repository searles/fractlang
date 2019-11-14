package at.searles.meelan

enum class BaseTypes: Type {
    Int {
        override fun commonType(type: Type): Type? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): kotlin.Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Real {
        override fun commonType(type: Type): Type? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): kotlin.Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Cplx {
        override fun commonType(type: Type): Type? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): kotlin.Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Bool {
        override fun commonType(type: Type): Type? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): kotlin.Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, Unit {
        override fun commonType(type: Type): Type? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): kotlin.Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }, String {
        override fun commonType(type: Type): Type? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun canConvert(node: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun convert(node: Node): Node {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun byteSize(): kotlin.Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}