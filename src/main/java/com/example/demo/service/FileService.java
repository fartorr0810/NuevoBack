package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
/**
 * Interfaz con los metodos mas usados
 * @author fran
 *
 */
public interface FileService {
	/**
	 * Metodo init
	 */
	public void init();
	/**
	 * Recibe un objeto MultiPart y puede lanzar IOExceptions, esto devuelve un array
	 * de bytes
	 * @param file archivo
	 * @return devuelve el array de bytes
	 * @throws IOException error en el archivo
	 */
	public byte[] save(MultipartFile file) throws IOException;
	/**
	 * Borrar todos los archivos
	 */
	public void deleteAll();
    public Stream<Path> loadAll();
	public String borrarFile(String file);
	public Resource load(String file);
	
	
	
}
