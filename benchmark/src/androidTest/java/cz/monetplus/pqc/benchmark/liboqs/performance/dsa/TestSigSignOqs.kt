package sk.bajuso.benchmark.liboqs.performance.dsa

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.SignatureAlgorithm
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestSigSignOqs {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val message: ByteArray = ByteArray(32) { it.toByte() }

    @Test fun benchmarkMlDsa3Sign() = bench(PqcAlgorithm.Sig.MlDsa3)
    @Test fun benchmarkMlDsa5Sign() = bench(PqcAlgorithm.Sig.MlDsa5)

    @Test fun benchmarkSlhDsa3FastShaSign() = bench(PqcAlgorithm.Sig.SlhDsa3FastSha)
    @Test fun benchmarkSlhDsa5FastShaSign() = bench(PqcAlgorithm.Sig.SlhDsa5FastSha)
    @Test fun benchmarkSlhDsa3SmallShaSign() = bench(PqcAlgorithm.Sig.SlhDsa3SmallSha)
    @Test fun benchmarkSlhDsa5SmallShaSign() = bench(PqcAlgorithm.Sig.SlhDsa5SmallSha)

    @Test fun benchmarkSlhDsa3FastShakeSign() = bench(PqcAlgorithm.Sig.SlhDsa3FastShake)
    @Test fun benchmarkSlhDsa5FastShakeSign() = bench(PqcAlgorithm.Sig.SlhDsa5FastShake)
    @Test fun benchmarkSlhDsa3SmallShakeSign() = bench(PqcAlgorithm.Sig.SlhDsa3SmallShake)
    @Test fun benchmarkSlhDsa5SmallShakeSign() = bench(PqcAlgorithm.Sig.SlhDsa5SmallShake)

    @Test fun benchmarkMayo3Sign() = bench(PqcAlgorithm.Sig.Mayo3)
    //@Test fun benchmarkMayo5Sign() = bench(PqcAlgorithm.Sig.Mayo5)

    @Test fun benchmarkFalcon5Sign() = bench(PqcAlgorithm.Sig.Falcon5)
    @Test fun benchmarkFalcon5PaddedSign() = bench(PqcAlgorithm.Sig.Falcon5Padded)

    //@Test fun benchmarkCross3RsdpSmallSign() = bench(PqcAlgorithm.Sig.Cross3RsdpSmall)
    //@Test fun benchmarkCross5RsdpSmallSign() = bench(PqcAlgorithm.Sig.Cross5RsdpSmall)
    @Test fun benchmarkCross3RsdpFastSign() = bench(PqcAlgorithm.Sig.Cross3RsdpFast)
    @Test fun benchmarkCross5RsdpFastSign() = bench(PqcAlgorithm.Sig.Cross5RsdpFast)
    @Test fun benchmarkCross3RsdpBalancedSign() = bench(PqcAlgorithm.Sig.Cross3RsdpBalanced)
    //@Test fun benchmarkCross5RsdpBalancedSign() = bench(PqcAlgorithm.Sig.Cross5RsdpBalanced)

    //@Test fun benchmarkCross3RsdpgSmallSign() = bench(PqcAlgorithm.Sig.Cross3RsdpgSmall)
    //@Test fun benchmarkCross5RsdpgSmallSign() = bench(PqcAlgorithm.Sig.Cross5RsdpgSmall)
    @Test fun benchmarkCross3RsdpgFastSign() = bench(PqcAlgorithm.Sig.Cross3RsdpgFast)
    @Test fun benchmarkCross5RsdpgFastSign() = bench(PqcAlgorithm.Sig.Cross5RsdpgFast)
    @Test fun benchmarkCross3RsdpgBalancedSign() = bench(PqcAlgorithm.Sig.Cross3RsdpgBalanced)
    @Test fun benchmarkCross5RsdpgBalancedSign() = bench(PqcAlgorithm.Sig.Cross5RsdpgBalanced)

/*    @Test fun benchmarkSnova3Ps56x25x2Sign() = bench(PqcAlgorithm.Sig.Snova3Ps56x25x2)
    @Test fun benchmarkSnova3Ps49x11x3Sign() = bench(PqcAlgorithm.Sig.Snova3Ps49x11x3)
    @Test fun benchmarkSnova3Ps37x8x4Sign() = bench(PqcAlgorithm.Sig.Snova3Ps37x8x4)
    @Test fun benchmarkSnova3Ps24x5x5Sign() = bench(PqcAlgorithm.Sig.Snova3Ps24x5x5)
    @Test fun benchmarkSnova5Ps29x6x5Sign() = bench(PqcAlgorithm.Sig.Snova5Ps29x6x5)
    @Test fun benchmarkSnova5Ps60x10x4Sign() = bench(PqcAlgorithm.Sig.Snova5Ps60x10x4)*/

    @Test fun benchmarkUov3Sign() = bench(PqcAlgorithm.Sig.Uov3)
    @Test fun benchmarkUov5Sign() = bench(PqcAlgorithm.Sig.Uov5)
    @Test fun benchmarkUov3PkcSign() = bench(PqcAlgorithm.Sig.Uov3Pkc)
    @Test fun benchmarkUov5PkcSign() = bench(PqcAlgorithm.Sig.Uov5Pkc)
    @Test fun benchmarkUov3PkcSkcSign() = bench(PqcAlgorithm.Sig.Uov3PkcSkc)
    @Test fun benchmarkUov5PkcSkcSign() = bench(PqcAlgorithm.Sig.Uov5PkcSkc)

    private fun bench(alg: SignatureAlgorithm) {
        Oqs.createSignatureManager(alg).use { mgr ->
            mgr.generateKeyPair()

            benchmarkRule.measureRepeated {
                mgr.sign(message)
            }
        }
    }
}