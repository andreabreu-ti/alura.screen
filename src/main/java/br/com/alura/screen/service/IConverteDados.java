package br.com.alura.screen.service;

public interface IConverteDados {

	public <T> T obterDados(String json, Class<T> classe);
}
