package ru.rikmasters.gilty.core.data.operation

class RetryOperation<I, O>(
    private val max: Int,
    private val operation: Operation<I, O>
): Operation<I, O> {
    
    override suspend fun perform(input: I): O {
        if(max < 1) throw IllegalArgumentException("max должен быть больше или равен 1")
        
        var lastThrowable: Throwable? = null
        var current = 0
        
        while(current++ < max) try {
            
            return operation.perform(input)
            
        } catch(t: Throwable) {
            lastThrowable = t
            continue
        }
        
        throw lastThrowable!!
    }
}

fun <I, O> Operation<I, O>.retry(max: Int) =
    RetryOperation(max, this)