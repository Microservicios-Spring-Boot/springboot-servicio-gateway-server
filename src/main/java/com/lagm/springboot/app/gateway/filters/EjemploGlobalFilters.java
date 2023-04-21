package com.lagm.springboot.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class EjemploGlobalFilters implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(EjemploGlobalFilters.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		LOGGER.info("Ejecutando filtro PRE: Antes de enrutar la solicitud al microservicio");

		// Se simula pasar un token en la cabecera del request
		exchange.getRequest().mutate().headers(h -> h.add("token", "12356"));

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			LOGGER.info("Ejecutando filtro POST: Después de enrutar la solicitud al microservicio");
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(valor -> {
				exchange.getResponse().getHeaders().add("token", valor);
			});

			exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "rojo").build());
			exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
		}));
	}

	/*
	 * permite establecer un orden de ejecución específico para los filtros globales
	 * en Spring Cloud Gateway, lo que es útil para garantizar que las operaciones
	 * de pre y post procesamiento se realicen en el orden correcto. En este caso,
	 * el filtro tiene una prioridad alta y se ejecutará antes que otros filtros con
	 * una prioridad menor.
	 */
	@Override
	public int getOrder() {
		return -1;
	}

}
