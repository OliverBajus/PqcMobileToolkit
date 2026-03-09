package cz.monetplus.pqcdemoapp.domain.model

import com.aheaditec.architecture.domain.error.Failure

sealed class PqcFailure: Failure.FeatureFailure() {
    data object UnsupportedAlgorithm: PqcFailure()
    data class PqcError(val throwable: Throwable?, val message: String? = null): PqcFailure()
}