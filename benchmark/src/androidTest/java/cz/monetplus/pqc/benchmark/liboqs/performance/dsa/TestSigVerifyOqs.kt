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
class TestSigVerifyOqs {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val message: ByteArray = ByteArray(32) { it.toByte() }

    @Test fun benchmarkMlDsa3Verify() = bench(PqcAlgorithm.Sig.MlDsa3)
    @Test fun benchmarkMlDsa5Verify() = bench(PqcAlgorithm.Sig.MlDsa5)

    @Test fun benchmarkSlhDsa3FastShaVerify() = bench(PqcAlgorithm.Sig.SlhDsa3FastSha)
    @Test fun benchmarkSlhDsa5FastShaVerify() = bench(PqcAlgorithm.Sig.SlhDsa5FastSha)
    @Test fun benchmarkSlhDsa3SmallShaVerify() = bench(PqcAlgorithm.Sig.SlhDsa3SmallSha)
    @Test fun benchmarkSlhDsa5SmallShaVerify() = bench(PqcAlgorithm.Sig.SlhDsa5SmallSha)

    @Test fun benchmarkSlhDsa3FastShakeVerify() = bench(PqcAlgorithm.Sig.SlhDsa3FastShake)
    @Test fun benchmarkSlhDsa5FastShakeVerify() = bench(PqcAlgorithm.Sig.SlhDsa5FastShake)
    @Test fun benchmarkSlhDsa3SmallShakeVerify() = bench(PqcAlgorithm.Sig.SlhDsa3SmallShake)
    @Test fun benchmarkSlhDsa5SmallShakeVerify() = bench(PqcAlgorithm.Sig.SlhDsa5SmallShake)

    @Test fun benchmarkMayo3Verify() = bench(PqcAlgorithm.Sig.Mayo3)
    //@Test fun benchmarkMayo5Verify() = bench(PqcAlgorithm.Sig.Mayo5)

    @Test fun benchmarkFalcon5Verify() = bench(PqcAlgorithm.Sig.Falcon5)
    @Test fun benchmarkFalcon5PaddedVerify() = bench(PqcAlgorithm.Sig.Falcon5Padded)

    //@Test fun benchmarkCross3RsdpSmallVerify() = bench(PqcAlgorithm.Sig.Cross3RsdpSmall)
    //@Test fun benchmarkCross5RsdpSmallVerify() = bench(PqcAlgorithm.Sig.Cross5RsdpSmall)
    @Test fun benchmarkCross3RsdpFastVerify() = bench(PqcAlgorithm.Sig.Cross3RsdpFast)
    @Test fun benchmarkCross5RsdpFastVerify() = bench(PqcAlgorithm.Sig.Cross5RsdpFast)
    @Test fun benchmarkCross3RsdpBalancedVerify() = bench(PqcAlgorithm.Sig.Cross3RsdpBalanced)
   // @Test fun benchmarkCross5RsdpBalancedVerify() = bench(PqcAlgorithm.Sig.Cross5RsdpBalanced)
/*
    @Test fun benchmarkCross3RsdpgSmallVerify() = bench(PqcAlgorithm.Sig.Cross3RsdpgSmall)
    @Test fun benchmarkCross5RsdpgSmallVerify() = bench(PqcAlgorithm.Sig.Cross5RsdpgSmall)*/
    @Test fun benchmarkCross3RsdpgFastVerify() = bench(PqcAlgorithm.Sig.Cross3RsdpgFast)
    @Test fun benchmarkCross5RsdpgFastVerify() = bench(PqcAlgorithm.Sig.Cross5RsdpgFast)
    @Test fun benchmarkCross3RsdpgBalancedVerify() = bench(PqcAlgorithm.Sig.Cross3RsdpgBalanced)
    @Test fun benchmarkCross5RsdpgBalancedVerify() = bench(PqcAlgorithm.Sig.Cross5RsdpgBalanced)
/*

    @Test fun benchmarkSnova3Ps56x25x2Verify() = bench(PqcAlgorithm.Sig.Snova3Ps56x25x2)
    @Test fun benchmarkSnova3Ps49x11x3Verify() = bench(PqcAlgorithm.Sig.Snova3Ps49x11x3)
    @Test fun benchmarkSnova3Ps37x8x4Verify() = bench(PqcAlgorithm.Sig.Snova3Ps37x8x4)
    @Test fun benchmarkSnova3Ps24x5x5Verify() = bench(PqcAlgorithm.Sig.Snova3Ps24x5x5)
    @Test fun benchmarkSnova5Ps29x6x5Verify() = bench(PqcAlgorithm.Sig.Snova5Ps29x6x5)
    @Test fun benchmarkSnova5Ps60x10x4Verify() = bench(PqcAlgorithm.Sig.Snova5Ps60x10x4)
*/

    @Test fun benchmarkUov3Verify() = bench(PqcAlgorithm.Sig.Uov3)
    @Test fun benchmarkUov5Verify() = bench(PqcAlgorithm.Sig.Uov5)
    @Test fun benchmarkUov3PkcVerify() = bench(PqcAlgorithm.Sig.Uov3Pkc)
    @Test fun benchmarkUov5PkcVerify() = bench(PqcAlgorithm.Sig.Uov5Pkc)
    @Test fun benchmarkUov3PkcSkcVerify() = bench(PqcAlgorithm.Sig.Uov3PkcSkc)
    @Test fun benchmarkUov5PkcSkcVerify() = bench(PqcAlgorithm.Sig.Uov5PkcSkc)

    private fun bench(alg: SignatureAlgorithm) {
        Oqs.createSignatureManager(alg).use { mgr ->
            val keypair = mgr.generateKeyPair()
            val signature = mgr.sign(message)

            benchmarkRule.measureRepeated {
                mgr.verify(message, signature, keypair.public)
            }
        }
    }
}