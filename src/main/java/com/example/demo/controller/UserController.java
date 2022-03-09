package com.example.demo.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.exception.AlquilerNoEncontradoException;
import com.example.demo.exception.ApiError;
import com.example.demo.exception.ContenidoComentarioVacioException;
import com.example.demo.exception.EmailRepetidoException;
import com.example.demo.exception.ErrorAlquilerException;
import com.example.demo.exception.FacturaNoEncontrada;
import com.example.demo.exception.KmHoraException;
import com.example.demo.exception.ModeloVacioException;
import com.example.demo.exception.PatineteNoDisponibleException;
import com.example.demo.exception.PrecioHoraVacioException;
import com.example.demo.exception.SinPatinetesException;
import com.example.demo.exception.SinPermisoException;
import com.example.demo.model.Alquiler;
import com.example.demo.model.AlquilerDTO;
import com.example.demo.model.Comentario;
import com.example.demo.model.Factura;
import com.example.demo.model.FileMessage;
import com.example.demo.model.Patinete;
import com.example.demo.model.PatineteDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.AlquilerServiceDB;
import com.example.demo.service.ComentarioServiceDB;
import com.example.demo.service.CorreoService;
import com.example.demo.service.FacturaServiceDB;
import com.example.demo.service.FileServiceS;
import com.example.demo.service.PatineteServiceDB;
import com.fasterxml.jackson.databind.JsonMappingException;


@RestController
public class UserController {
//	Servicios inyectados
    @Autowired 
    private UserRepo userRepo;
    @Autowired
    private ComentarioServiceDB servicioComentario;
    @Autowired
    private PatineteServiceDB servicioPatinete;
    @Autowired 
    private AlquilerServiceDB servicioAlquiler;
    @Autowired
    private FileServiceS servicioarchivos;
    @Autowired
    private CorreoService serviciocorreo;
    @Autowired
    private FacturaServiceDB serviciofacturas;
    /**
     * Nos devuelve la id del usuario al hacer un get del usuario
     * @return la id del usuario
     */
    @GetMapping("/user")
    public ResponseEntity<Integer> getUserDetails(){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.status(HttpStatus.OK).body(userRepo.findByEmail(email).get().getId());
    }
    
	@GetMapping("/user/{email}")
	public User checkEmailUser(@PathVariable String email) {	
		if (userRepo.findByEmail(email)!=null) {
			return userRepo.findByEmail(email).orElse(null);
		}
		else {
			throw new EmailRepetidoException(email);
		}
	}
    /**
     * Metodo para comprobar que existe ese correo en la base de datos (NO UTILIZADO EN FRONT)
     * @param email 
     * @return devuelve true o false segun si lo encuentra o no
     */
    @PostMapping("/comprobar-email")
    public ResponseEntity<Boolean>comprobarEmail(@RequestBody String email){
    	User user=this.userRepo.findByEmail(email).orElse(null);
    	if (user==null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(true);
    	}else {
    		return ResponseEntity.status(HttpStatus.IM_USED).body(false);
    	}
    }
    /**
     * Devuelve toda la lista de comentarios existentes
     * @return la lsita de comentarios
     */
    @GetMapping("/comentario")
    public ResponseEntity<List<Comentario>> devolverListaComentario(){
    	return ResponseEntity.status(HttpStatus.IM_USED).body(this.servicioComentario.findAll());
    }
    /**
     * Recibe un objeto comentario,tras ello lo introduce en la base de datos
     * @param c Objeto comentario
     * @return nos devuelvejel comentario.
     */
    @PostMapping("/comentario")
    public ResponseEntity<Comentario> recibirComentario(@RequestBody Comentario c){
    	if (c.getContenidocomentario()==null) {
    		throw new ContenidoComentarioVacioException();
    	}
    	if (c.getContenidocomentario().isEmpty()) {
    		throw new ContenidoComentarioVacioException();
    	}
    	this.servicioComentario.addComentario(c);
    	this.serviciocorreo.enviarConGMail("frankarroyop@gmail.com", "Contacto", c.getContenidocomentario()+""
    			+ "Telefono : "+c.getTelefono()+"Correo de: "+c.getEmail());
    	return ResponseEntity.status(HttpStatus.OK).body(c);
    }
    /**
     * Metodo post para anadir un patinete a la base de datos
     * @param p objeto patinete
     * @return devuelve el json con el patinete y su estado
     */
    @PostMapping("/patinete")
    public ResponseEntity<Patinete> addPatinete(@RequestBody PatineteDTO p){
    	Patinete patin=new Patinete(p.getModelo(), p.getPrecioHora(), p.getDisponible(), p.getKmhora(), p.getImagen());
    	this.servicioPatinete.addPatinete(patin);
 
    	return ResponseEntity.status(HttpStatus.OK).body(patin);
    }
    /**
     * Metodo para subir un archivo, los parametros del patinete viajan por la url y 
     * en el body la imagen. Despues llama al controlador con el metodo addPatinete que introducira
     * el patinete en la base de datos
     * @param file archivo
     * @param modelo
     * @param precioHora
     * @param kmhora
     * @return devuelve un subido con exito si esta correcto , si no, error.
     */
    @PostMapping("/subida")
    public ResponseEntity<FileMessage> subidaArchivo(@RequestBody MultipartFile file
    		,@RequestParam String modelo,
    		@RequestParam Double precioHora,
    		@RequestParam Double kmhora
    		){
    	if (modelo.isEmpty()) {
    		throw new ModeloVacioException();
    	}
    	if (precioHora==0 || precioHora<0) {
    		throw new PrecioHoraVacioException();
    	}
    	if (kmhora==0 || kmhora<0) {
    		throw new KmHoraException();
    	}
    	try {
    	byte[] imagen=this.servicioarchivos.save(file);
    		PatineteDTO patinete=new PatineteDTO(modelo,precioHora,kmhora,imagen);
    		patinete.setImagen(imagen);
    		addPatinete(patinete);
    		return ResponseEntity.status(HttpStatus.OK).body(new FileMessage("Subido con exito"));
    	}catch(Exception ex) {
    		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED
    				).body(new FileMessage("Error al subir imagen"));
    	}
    }
    /**
     * Devuelve una lista de patinetes, si no le anadimos un filtro, por defecto tendra  ese filtro
     * con el valor ALL, si se le pasa el filtro con disponible, nos devolvera una lista
     * con los patinetes disponibles
     * @param filtro 
     * @return nos devuelve la lista de patinetes, en caso de que no haya patinetes , nos devolvera
     * una excepcion.
     */
    @GetMapping("/patinete")
    public ResponseEntity<List<Patinete>> obtenerPatinetes(
    		@RequestParam(name="filtro",defaultValue="ALL") String filtro){
    	if ("disponible".equals(filtro)) {
    		List<Patinete> listapatinetes= this.servicioPatinete.findPatinetesDisponibles();
    		if (listapatinetes.isEmpty()) {
    			throw new PatineteNoDisponibleException();
    		}else {
    			return ResponseEntity.status(HttpStatus.OK).body(listapatinetes);
    		}
    	}else{
    		List<Patinete> listapatinetes= this.servicioPatinete.findAll();
    		if (listapatinetes.isEmpty()) {
    			throw new PatineteNoDisponibleException();
    		}else {
    			return ResponseEntity.status(HttpStatus.OK).body(listapatinetes);
    		}
    	}
    	}
    /**
     * Metodo para anadir un alquiler, y nos devuelve un json con los datos que si deberia 
     * ver el usuario y no otros datos que no deberia ver.
     * @param alquiler objeto alquiler con los datos que se necesitan
     * @return devuelve el DTO y si hay algun problema , como si el patinete no estuviese
     * disponible porque alguien lo alquilo antes, salta la excepcion.
     */
    @PostMapping("/alquiler")
    public ResponseEntity<AlquilerDTO> crearAlquiler(@RequestBody Alquiler alquiler){
    	if (alquiler.getHorasalquiler()==null || alquiler.getPatinete()==null || alquiler.getUser()==null
    			||alquiler.getHoraentrega()==null) {
    		throw new ErrorAlquilerException();
    	}
    	try {
    		AlquilerDTO result=this.servicioAlquiler.addAlquiler(alquiler);
    		return ResponseEntity.status(HttpStatus.OK).body(result);    		
    	}catch (Exception ex) {
    		throw new PatineteNoDisponibleException();
    	}
    }
    /**
     * Metodo para calcular el precio del alquiler y la fecha de eentrega para mostrarlo en el front
     * y no tenga que asumir esa logica el cliente
     * @param alquiler objeto con los datos del alquiler
     * @return devuelve el DTO con los datos.
     */
    @PostMapping("/calcular-alquiler")
    public ResponseEntity<AlquilerDTO> calcularPrecio(@RequestBody Alquiler alquiler){
    	try {
    		AlquilerDTO result=this.servicioAlquiler.calcularPrecio(alquiler);    		
    		return ResponseEntity.status(HttpStatus.OK).body(result);
    	}catch(Exception ex) {
    		throw new PatineteNoDisponibleException();
    	}    	
    }
    /**
     * Obtiene una lista de alquileres 
     * @return devuelve al lista de alquileres de todos los usuarios
     */
    @GetMapping("/alquiler")
    public ResponseEntity<List<AlquilerDTO>> listarTodosAlquileres(){
    		List<Alquiler>listaalquileres=this.servicioAlquiler.findAll();
    		List<AlquilerDTO> result=this.servicioAlquiler.conversorAlquilerAdto(listaalquileres);
    	return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     * Devuelve un alquiler de un usuario especifico pasandoselo por la id
     * @param id del usuario
     * @return devuelve una lista de alquileres del usuario indicado
     */
    @GetMapping("/alquiler/{id}")
    public ResponseEntity<List<AlquilerDTO>> listaAlquilerUser(@PathVariable Integer id){
    	List<Alquiler> listaalquiler=this.servicioAlquiler.findByUser(id);
    	List<AlquilerDTO> result=this.servicioAlquiler.conversorAlquilerAdto(listaalquiler);
    	if (result.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    	}
    	return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     * Crea una factura de un alquiler
     * @param id del alquiler
     * @param f factura
     * @return devuelve la factura
     */
    @PostMapping("/alquiler/{id}/factura")
    public ResponseEntity<Factura> crearFactura(@PathVariable Integer id,@RequestBody Factura f){
    	this.serviciofacturas.addFactura(f);
    	return ResponseEntity.status(HttpStatus.CREATED).body(f);
    }
    /**
     * Devuelve una lista de facturas que tenga un alquiler 
     * @param idalquiler id del alquiler
     * @return devuelve lista de alquileres
     */
    @GetMapping("/alquiler/{idalquiler}/factura")
    public ResponseEntity<List<Factura>> todasLasFacturas(@PathVariable Integer idalquiler){
    	List<Factura> listafacturas=this.serviciofacturas.buscarTodasFacturas(idalquiler);
    	return ResponseEntity.status(HttpStatus.OK).body(listafacturas);
    }
    /**
     * Edita la factura especifica de un alquiler 
     * @param idalquiler de alquiler
     * @param idfactura de id factura
     * @param f de factura
     * @return devuelve la factura
     */
    @PutMapping("alquiler/{idalquiler}/factura/{idfactura}")
    public ResponseEntity<Factura> editarFactura(@PathVariable Integer idalquiler,@PathVariable Integer idfactura,
    		@RequestBody Factura f){
    	Factura nuevafactura=this.serviciofacturas.findById(idfactura);
    	Alquiler alq=this.servicioAlquiler.findById(idalquiler);
    	if (nuevafactura==null || alq==null) {
    		throw new FacturaNoEncontrada();
    	}else {
    		nuevafactura.setPrecio(f.getPrecio());
    		this.serviciofacturas.edit(nuevafactura);
    		return ResponseEntity.status(HttpStatus.ACCEPTED).body(nuevafactura);
    	}
    }
    /**
     * Elimina una factura
     * @param idalquiler del alquiler
     * @param idfactura de factura 
     */
    @DeleteMapping("/alquiler/{idalquiler}/factura/{idfactura}")
    public ResponseEntity borrarFactura(@PathVariable Integer idalquiler,@PathVariable Integer idfactura) {
    	Factura nuevafactura=this.serviciofacturas.findById(idfactura);
    	Alquiler alq=this.servicioAlquiler.findById(idalquiler);
    	if (nuevafactura==null || alq==null) {
    		throw new FacturaNoEncontrada();
    	}else {
    		this.serviciofacturas.deleteFacturaById(nuevafactura.getIdfactura());
    		return ResponseEntity.status(HttpStatus.ACCEPTED).body(nuevafactura);
    	}
    }
    /**
     * Metodo que devuelve el patinete del alquiler que se le indica. 
     * @param id del alquiler
     * @return el patinete
     */
    @GetMapping("alquiler/{id}/patinete")
    public ResponseEntity<Patinete> devolverPatinete(@PathVariable Integer id){
    	Alquiler alq=this.servicioAlquiler.findById(id);
    	if (alq!=null) {
    		Patinete pat=this.servicioPatinete.findById(alq.getPatinete().getIdpatinete());
    		return ResponseEntity.status(HttpStatus.OK).body(pat);
    	}else {
    		throw new AlquilerNoEncontradoException();
    	}
    }
    /**
     * Metodo para entregar el patinete
     * @param id del alquiler
     * @return devuelve el alquiler con el estado cambiado
     */
    @CrossOrigin(origins = "http://localhost:4200/alquiler")
    @PutMapping("/alquiler/{id}")
    public ResponseEntity<AlquilerDTO> entregarPatinete(@PathVariable Integer id){
    	Alquiler alquiler=this.servicioAlquiler.findById(id);
    	if (alquiler==null) {
    		throw new AlquilerNoEncontradoException();
    	}
    	AlquilerDTO alq=this.servicioAlquiler.conversorAlquilerAdtoIndivudual(alquiler);
    	this.servicioAlquiler.entregarPatinete(id);
    	return ResponseEntity.status(HttpStatus.OK).body(alq);
    }
    /**
     * Metodo para eliminar un alquiler especifico
     * @param id del alquiler a eliminar 
     * @return devuelve un NO_CONTENT
     */
    @CrossOrigin(origins = "http://localhost:4200/alquiler")
    @DeleteMapping("/alquiler/{id}")
    public ResponseEntity<AlquilerDTO> deleteAlquiler(@PathVariable Integer id){
    	Alquiler alquiler=this.servicioAlquiler.findById(id);
    	if (alquiler==null) {
    		throw new AlquilerNoEncontradoException();
    	}
    	AlquilerDTO alq=this.servicioAlquiler.conversorAlquilerAdtoIndivudual(alquiler);
    	User user=this.userRepo.findById(alquiler.getUser().getId()).orElse(null);
    	if (user!=null) {
    		user.getListaalquiler().remove(alquiler);
    		this.userRepo.save(user);
    		this.servicioAlquiler.eliminarAlquiler(id);
    	}
    	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(alq);
    }
    /**
     * Error en caso de que no se encuentre el alquiler
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(AlquilerNoEncontradoException.class)
    public ResponseEntity<ApiError> handleAlquilerNoEncontrado(AlquilerNoEncontradoException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.NOT_FOUND);
    	apierror.setFecha(LocalDateTime.now());
    	apierror.setMensaje(ex.getMessage());
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apierror);
    }
    /**
     * Error en caso que no se encuentre la factura
     * @param ex excepcion
     * @return devuelve el api error con los datos de la excepcion
     */
    @ExceptionHandler(FacturaNoEncontrada.class)
    public ResponseEntity<ApiError> handleFacturaNoEncontrada(FacturaNoEncontrada ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.NOT_FOUND);
    	apierror.setFecha(LocalDateTime.now());
    	apierror.setMensaje(ex.getMessage());
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apierror);
    }
    /**
     * Error en caso de el modelo al anadir patinete este vacio
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(ModeloVacioException.class)
    public ResponseEntity<ApiError> modeloPatineteVacioHandler (ModeloVacioException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.BAD_REQUEST);
    	apierror.setFecha(LocalDateTime.now());
    	apierror.setMensaje(ex.getMessage());
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apierror);
    }
    /**
     * Error en caso de que el contenido del comentario este vacio
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(ContenidoComentarioVacioException.class)
    public ResponseEntity<ApiError> handlerComentario(ContenidoComentarioVacioException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.BAD_REQUEST);
    	apierror.setFecha(LocalDateTime.now());
    	apierror.setMensaje(ex.getMessage());
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apierror);
    }
    /**
     * Error en caso de que la velocidad sea nula,cero o negativa
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(KmHoraException.class)
    public ResponseEntity<ApiError> handlerVelocidad(KmHoraException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.BAD_REQUEST);
    	apierror.setFecha(LocalDateTime.now());
    	apierror.setMensaje(ex.getMessage());
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apierror);
    }
    /**
     * Error en caso de que falten datos al alquilar un patinete
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(ErrorAlquilerException.class)
    public ResponseEntity<ApiError> errorAlquilerHandler(ErrorAlquilerException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.BAD_REQUEST);
    	apierror.setFecha(LocalDateTime.now());
    	apierror.setMensaje(ex.getMessage());
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apierror);
    }
    /**
     * Error en caso de que el precio por hora sea negativo o 0 o nulo
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(PrecioHoraVacioException.class)
    public ResponseEntity<ApiError> precioHoraHandler(PrecioHoraVacioException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.BAD_REQUEST);
    	apierror.setFecha(LocalDateTime.now());
    	apierror.setMensaje(ex.getMessage());
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apierror);
    }
    /**
     * Error en caso de que el JSON no tuviera el formato valido
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ApiError> handleJsonMappingException(JsonMappingException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.BAD_REQUEST);
    	apierror.setMensaje(ex.getMessage());
    	apierror.setFecha(LocalDateTime.now());
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apierror);
    }
    /**
     * Error en caso de que no haya patinetes
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(SinPatinetesException.class)
    public ResponseEntity<ApiError> handleQueNoTienePatinete(SinPatinetesException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.NOT_FOUND);
    	apierror.setMensaje(ex.getMessage());
    	apierror.setFecha(LocalDateTime.now());
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apierror);
    }
    /**
     * Error en caso de que el patinete no este disponible
     * @param ex excepcion
     * @return devuelve el apierror con los datos de la excepcion
     */
    @ExceptionHandler(PatineteNoDisponibleException.class)
    public ResponseEntity<ApiError> handleAlquiler(PatineteNoDisponibleException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.NOT_FOUND);
    	apierror.setMensaje(ex.getMessage());
    	apierror.setFecha(LocalDateTime.now());
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apierror);
    }
    
    /**
     * Error en caso de que no tenga permiso para realizar esa accion
     * @param ex excepcion 
     * @return devuelve error con un notfound
     */
    @ExceptionHandler(SinPermisoException.class)
    public ResponseEntity<ApiError> handleSinPermiso(SinPermisoException ex){
    	ApiError apierror=new ApiError();
    	apierror.setEstado(HttpStatus.FORBIDDEN);
    	apierror.setMensaje(ex.getMessage());
    	apierror.setFecha(LocalDateTime.now());
    	return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apierror);
    }
}