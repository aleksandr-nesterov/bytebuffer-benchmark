package benchmarks;

import com.spaniard.ByteBufferAccount;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Measurement(iterations = 10)
@Warmup(iterations = 3)
@State(Scope.Thread)
public class AccountBenchmark {

    private static final int TRANSACTIONS = 1_000_000;

    @Benchmark
    public void heapByteBuffer() {
        ByteBufferAccount byteBufferAccount = new ByteBufferAccount(false);
        for (int i = 0; i < TRANSACTIONS; i++) {
            byteBufferAccount.register(i, 1, false);
        }
        byteBufferAccount.balance();
        byteBufferAccount.free();
    }

    @Benchmark
    public void offheapByteBuffer() {
        ByteBufferAccount byteBufferAccount = new ByteBufferAccount(true);
        for (int i = 0; i < TRANSACTIONS; i++) {
            byteBufferAccount.register(i, 1, false);
        }
        byteBufferAccount.balance();
        byteBufferAccount.free();
    }
}
