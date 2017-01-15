package com.roxiemobile.networkingapi.network.rest.response.error;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Consumer;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

abstract class AbstractRestApiError implements RestApiError
{
// MARK: - Construction

    AbstractRestApiError(Throwable cause) {
        // Init instance variables
        mCause = cause;
    }

// MARK: - Properties

    @Override
    public Throwable getCause() {
        return mCause;
    }

// MARK: - Methods

    /**
     * Sends a printable representation of this {@code RestApiError}'s description
     * to the consumer.
     */
    @Override
    public void printErrorDescription(@NonNull Consumer<String> consumer) {
        requireNotNull(consumer, "consumer is null");

        if (mCause != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            // Compose error description
            printWriter.write(mCause.getMessage() + ": ");
            mCause.printStackTrace(printWriter);
            printWriter.flush();

            // Send error description to consumer
            consumer.accept(stringWriter.toString());
        }
    }

// MARK: - Variables

    private Throwable mCause;

}
