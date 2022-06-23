/*
 * #%L
 * de-metas-camel-externalsystems-core
 * %%
 * Copyright (C) 2022 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package de.metas.camel.externalsystems.core;

import com.sun.istack.Nullable;
import de.metas.camel.externalsystems.core.restapi.auth.ExternalSystemAuthTokenMapping;
import de.metas.common.externalsystem.JsonExternalSystemRequest;
import de.metas.common.util.Check;
import de.metas.common.util.CoalesceUtil;
import de.metas.common.util.EmptyUtil;
import de.metas.common.util.FileUtil;
import lombok.NonNull;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.metas.camel.externalsystems.common.ExternalSystemCamelConstants.ERROR_WRITE_TO_ADISSUE;
import static de.metas.camel.externalsystems.common.ExternalSystemCamelConstants.HEADER_AUDIT_TRAIL;
import static de.metas.camel.externalsystems.common.ExternalSystemCamelConstants.HEADER_EXTERNAL_SYSTEM_VALUE;
import static de.metas.camel.externalsystems.common.ExternalSystemCamelConstants.HEADER_PINSTANCE_ID;
import static de.metas.camel.externalsystems.common.ExternalSystemCamelConstants.HEADER_TRACE_ID;
import static de.metas.camel.externalsystems.core.CoreConstants.API_BASE_URL;
import static de.metas.camel.externalsystems.core.CoreConstants.API_V2_BASE_URL;
import static org.apache.camel.builder.Builder.simple;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.file;

public class MetasfreshAuthorizationTokenNotifier extends EventNotifierSupport
{
	private static final Logger logger = Logger.getLogger(MetasfreshAuthorizationTokenNotifier.class.getName());

	private final ExternalSystemAuthTokenMapping authTokenMapping = new ExternalSystemAuthTokenMapping();

	private final ProducerTemplate producerTemplate;

	private final String defaultAuthToken;

	public MetasfreshAuthorizationTokenNotifier(@NonNull final ProducerTemplate producerTemplate, @Nullable final String defaultAuthToken)
	{
		this.producerTemplate = producerTemplate;
		this.defaultAuthToken = defaultAuthToken;
	}

	@Override
	public void notify(@NonNull final CamelEvent event)
	{
		try
		{
			if (event instanceof CamelEvent.ExchangeSendingEvent)
			{
				final CamelEvent.ExchangeSendingEvent sendingEvent = (CamelEvent.ExchangeSendingEvent)event;

				handleSendingEvent(sendingEvent.getExchange());
			}
			// else if (event instanceof CamelEvent.ExchangeSentEvent)
			// {
			// 	final CamelEvent.ExchangeSentEvent sentEvent = (CamelEvent.ExchangeSentEvent)event;
			//
			// 	handleSentEvent(sentEvent.getExchange());
			// }
		}
		catch (final Exception exception)
		{
			final Exchange sourceExchange = ((CamelEvent.ExchangeEvent)event).getExchange();

			if (sourceExchange == null
					|| sourceExchange.getIn() == null
					|| sourceExchange.getIn().getHeader(HEADER_PINSTANCE_ID) == null)
			{
				logger.log(Level.SEVERE, "Audit failed! and no pInstance could be obtained from source exchange!");
				return;
			}

			final Map<String, Object> headers = new HashMap<>();
			headers.put(HEADER_PINSTANCE_ID, sourceExchange.getIn().getHeader(HEADER_PINSTANCE_ID, Object.class));
			headers.put(Exchange.EXCEPTION_CAUGHT, exception);

			producerTemplate.sendBodyAndHeaders("direct:" + ERROR_WRITE_TO_ADISSUE, null, headers);
		}
	}

	private void handleSentEvent(@NonNull final Exchange exchange)
	{
		// final String auditTrailEndpoint = exchange.getIn().getHeader(HEADER_AUDIT_TRAIL, String.class);
		// if (EmptyUtil.isEmpty(auditTrailEndpoint))
		// {
		// 	return;
		// }

		final String endpointURI = "";
		final JsonExternalSystemRequest request = exchange.getIn().getBody(JsonExternalSystemRequest.class);

		if (endpointURI.contains(simple(API_BASE_URL).toString()) || endpointURI.contains(simple(API_V2_BASE_URL).toString()))
		{

			if (request != null && request.getExternalSystemConfigId() != null)
			{
				if (authTokenMapping.retreiveToken(request.getExternalSystemConfigId()) != null)
				{
					exchange.getIn().setHeader(CoreConstants.AUTHORIZATION, authTokenMapping.retreiveToken(request.getExternalSystemConfigId()));
				}
				else
				{
					exchange.getIn().setHeader(CoreConstants.AUTHORIZATION, defaultAuthToken);
				}
			}
		}

		// producerTemplate.sendBodyAndHeaders(getAuditEndpoint(exchange), exchange.getIn().getBody(), exchange.getIn().getHeaders());
	}

	private void handleSendingEvent(@NonNull final Exchange exchange)
	{
		final Object exchangeBody = exchange.getIn().getBody();

		final JsonExternalSystemRequest externalSystemRequest = exchange.getIn().getBody(JsonExternalSystemRequest.class);


		// if (request != null && EmptyUtil.isNotBlank(request.getAuthToken()) && !request.getAuthToken().equals("<secret>"))
		// {
		// 	exchange.getIn().setHeader(CoreConstants.AUTHORIZATION, request.getAuthToken());
		// }
		// else
		// {
		// 	exchange.getIn().setHeader(CoreConstants.AUTHORIZATION, defaultAuthToken);
		// }

		if (externalSystemRequest != null && EmptyUtil.isNotBlank(externalSystemRequest.getAuthToken()) && !externalSystemRequest.getAuthToken().equals("<secret>"))
		{
			exchange.getIn().setHeader(CoreConstants.AUTHORIZATION, externalSystemRequest.getAuthToken());
		}

		// producerTemplate.sendBodyAndHeaders(String.valueOf(exchange.getProperties().get("CamelToEndpoint")), exchange.getIn().getBody(), exchange.getIn().getHeaders());
	}

	@NonNull
	private static String extractEndpointURI(@Nullable final Endpoint endpoint)
	{
		return endpoint != null && Check.isNotBlank(endpoint.getEndpointUri())
				? endpoint.getEndpointUri()
				: "[Could not obtain endpoint information]";
	}

	@NonNull
	private static String getAuditEndpoint(@NonNull final Exchange exchange)
	{
		final String auditTrailEndpoint = exchange.getIn().getHeader(HEADER_AUDIT_TRAIL, String.class);
		if (EmptyUtil.isEmpty(auditTrailEndpoint))
		{
			throw new RuntimeCamelException("auditTrailEndpoint cannot be empty at this point!");

		}

		final String externalSystemValue = exchange.getIn().getHeader(HEADER_EXTERNAL_SYSTEM_VALUE, String.class);

		final String value = FileUtil.stripIllegalCharacters(externalSystemValue);

		final String traceId = CoalesceUtil.coalesceNotNull(exchange.getIn().getHeader(HEADER_TRACE_ID, String.class), UUID.randomUUID().toString());

		final String auditFolderName = DateTimeFormatter.ofPattern("yyyy-MM-dd")
				.withZone(ZoneId.systemDefault())
				.format(LocalDate.now());

		final String auditFileNameTimeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmssSSS")
				.withZone(ZoneId.systemDefault())
				.format(Instant.now());

		final String auditFileName = traceId + "_" + value + "_" + auditFileNameTimeStamp + ".txt";

		return file(auditTrailEndpoint + "/" + auditFolderName + "/?fileName=" + auditFileName).getUri();
	}
}
