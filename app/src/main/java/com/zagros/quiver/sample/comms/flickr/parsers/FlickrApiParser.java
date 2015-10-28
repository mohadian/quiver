package com.zagros.quiver.sample.comms.flickr.parsers;

import com.zagros.quiver.rest.core.http.AbstractErrorResult;
import com.zagros.quiver.rest.core.parser.AbstractJsonResponseParser;
import com.zagros.quiver.rest.core.parser.ParserException;
import com.zagros.quiver.sample.comms.flickr.api.FlickrConstants;

import org.json.JSONException;

/**
 * Created by Mostafa on 25/10/2015.
 */
public class FlickrApiParser extends AbstractJsonResponseParser {
    protected AbstractErrorResult mParsedError;

    @Override
    public void parse() throws ParserException {
        super.parse();
        setIsSuccessful(true);
    }

    @Override
    public void parseError() throws ParserException {
        super.parseError();
        setIsSuccessful(false);
        if (mRestResponse != null) {
            AbstractErrorResult error = new AbstractErrorResult();
            error.setContent(mRestResponse.toString());
            if (!mRestResponse.isNull(FlickrConstants.API_ERROR_CODE)) {
                try {
                    error.setErrorCode(mRestResponse.getString(FlickrConstants.API_ERROR_CODE));
                } catch (JSONException e) {
                    throw new ParserException(e);
                }
            }

            setParsedError(error);
        }
    }

    public AbstractErrorResult getParsedError() {
        return mParsedError;
    }

    public void setParsedError(AbstractErrorResult aParsedError) {
        mParsedError = aParsedError;
    }

}
