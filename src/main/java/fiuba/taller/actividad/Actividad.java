package fiuba.taller.actividad;

import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis2.AxisFault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ws.services.*;

@SuppressWarnings("unused")
public class Actividad implements Serializable {

	protected long id;
	protected long idAmbitoSuperior;
	protected long idActividadSuperior;
	protected String nombre;
	protected String tipo;
	protected String descripcion;
	protected long fechaInicio;
	protected long fechaFin;
	protected List<Long> coordinadores;
	protected long idMuro;
	protected long idCartelera;
	protected long idForo;
	protected List<Long> actividadesInternas;
	protected long idChat;

	private final static String className = Actividad.class.getSimpleName();

	public Actividad() {
		id = -1;
		idAmbitoSuperior = -1;
		idActividadSuperior = -1;
		nombre = "";
		tipo = "";
		descripcion = "";
		fechaFin = -1;
		fechaInicio = -1;
	}

	public long getId() {
		return id;
	}

	public long getIdAmbitoSuperior() {
		return idAmbitoSuperior;
	}

	public void setIdAmbitoSuperior(long idAmbitoSuperior) {
		this.idAmbitoSuperior = idAmbitoSuperior;
	}

	public long getIdActividadSuperior() {
		return idActividadSuperior;
	}

	public void setIdActividadSuperior(long idActividadSuperior) {
		this.idActividadSuperior = idActividadSuperior;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public long getFechaInicio() {
		return fechaInicio;
	}

	public long getFechaFin() {
		return fechaFin;
	}

	public void setFecha(long fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public void setFecha(long fechaInicio, long fechaFin)
			throws RemoteException {
		if (fechaInicio > fechaFin) {
			String mensaje = "La fecha de inicio no puede ser posterior a la "
					+ "fecha de fin";
			throw new RemoteException(mensaje);
		}
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	@Deprecated
	// FIXME De esto no se encarga Participacion???
	public List<String> getCoordinadores() {
		// TODO(Pampa) Implementar
		return new ArrayList<>();
	}

	@Deprecated
	// FIXME De esto no se encarga Participacion???
	public void agregarCoordinador(long idCoordinador) {
		/*
		 * TODO(Pampa) Implementar. Si el id ya existe, debe lanzar una
		 * excepcion.
		 */
	}

	@Deprecated
	// FIXME De esto no se encarga Participacion???
	public void eliminarCoordinador(long idCoordinador) {
		/*
		 * TODO(Pampa) Implementar. Si el id no existe, debe lanzar una
		 * excepcion. Si es el único coordinador, no se puede borrar. Lanzar una
		 * excepcion.
		 */
	}

	public long getIdMuro() {
		return idMuro;
	}

	public long getIdCartelera() {
		return idCartelera;
	}

	public long getIdForo() {
		return idForo;
	}

	public List<Actividad> getActividades() {
		// TODO(Pampa) Implementar
		return new ArrayList<>();
	}

	public long getIdChat() {
		return idChat;
	}

	public void actualizar(String xml) throws RemoteException {
		Actividad actividadTemporal = new Actividad();
		actividadTemporal.descerializar(xml);
		actualizar(actividadTemporal);
	}

	// serializa a la actividad
	// devuelve xml
	// este metodo intenta ser util tanto para cuando:
	// -> se envian las propiedades de la actividad a presentacion
	// -> se guardan los datos a integracion y se envian los datos de consulta a
	// integracion
	@Override
	public String serializar() {
		return "<?xml version=\"1.0\"?><WS><Actividad>" + serializarInterno()
				+ "</Actividad></WS>";
	}

	@Override
	public void descerializar(String xml) throws RemoteException {
		Document doc = ParserXml.getDocumentElement(xml);
		descerializar(doc);
	}

	@Override
	public void guardarNuevoEstado() throws RemoteException {
		IntegracionStub servicio = new IntegracionStub();
		GuardarDatos envio = new GuardarDatos();
		String xml = serializar();
		envio.setXml(xml);
		GuardarDatosResponse response = servicio.guardarDatos(envio);
		String retorno = response.get_return();
		String idStr = ParserXml.procesarNotificacionIntegracion(className,
				retorno);
		id = Long.valueOf(idStr);
	}

	@Override
	public void eliminarEstado() throws RemoteException {
		IntegracionStub servicio = new IntegracionStub();
		EliminarDatos envio = new EliminarDatos();
		String xml = "<WS><Actividad><id>" + Long.toString(id)
				+ "</id></Actividad></WS>";
		envio.setXml(xml);
		EliminarDatosResponse respuesta = servicio.eliminarDatos(envio);
		String retorno = respuesta.get_return();
		ParserXml.procesarNotificacionIntegracion(className, retorno);
	}

	@Override
	public void actualizarEstado() throws RemoteException {
		IntegracionStub servicio = new IntegracionStub();
		ActualizarDatos envio = new ActualizarDatos();
		String xml = serializar();
		envio.setXml(xml);
		ActualizarDatosResponse respuesta = servicio.actualizarDatos(envio);
		String retorno = respuesta.get_return();
		ParserXml.procesarNotificacionIntegracion(className, retorno);
	}

	/**
	 * A partir de los valores de los atributos internos de la instancia, genera
	 * un XML que es enviado a Integracion. Integracion devuelve un XML con
	 * todos los datos y este es devuelto. NOTA: integracion puede devolver mas
	 * de 1 XML según los valores de los parametros internos de la instancia.
	 */
	public String realizarConsulta() throws RemoteException {
		IntegracionStub servicio = new IntegracionStub();
		SeleccionarDatos envio = new SeleccionarDatos();
		String xml = serializar();
		envio.setXml(xml);
		SeleccionarDatosResponse respuesta = servicio.seleccionarDatos(envio);
		String retorno = respuesta.get_return();
		return retorno;
	}

	/* METODOS DE CLASE (ESTATICOS) */

	public static Actividad getActividad(long idActividad)
			throws RemoteException {
		Actividad actividad = new Actividad();
		actividad.descerializar(getPropiedades(idActividad));
		return actividad;
	}

	public static String getPropiedades(long idActividad)
			throws RemoteException {
		IntegracionStub servicio = new IntegracionStub();
		SeleccionarDatos envio = new SeleccionarDatos();
		String xml = "<WS><Actividad>"
				+ "<id>" + idActividad + "</id>"
				+ "</Actividad></WS>";
		envio.setXml(xml);
		SeleccionarDatosResponse respuesta = servicio.seleccionarDatos(envio);
		String retorno = respuesta.get_return();
		procesarConsultaIndividualIntegracion(retorno);
		return retorno;
	}

	/* METODOS PROTEGIDOS AUXILIARES */

	protected void setId(long id) {
		this.id = id;
	}

	protected String serializarInterno() {
		String xml = "";
		if (id > 0) {
			String idStr = String.valueOf(id);
			xml = "<id>" + idStr + "</id>";
		}
		if (nombre.length() > 0) {
			xml += "<nombre>" + nombre + "</nombre>";
		}
		if (tipo.length() > 0) {
			xml += "<tipo>" + tipo + "</tipo>";
		}
		if (idAmbitoSuperior > 0) {
			String idAmbSupStr = String.valueOf(idAmbitoSuperior);
			xml += "<ambitoSuperiorId>" + idAmbSupStr + "</ambitoSuperiorId>";
		}
		if (idActividadSuperior > 0) {
			String idActSupStr = String.valueOf(idActividadSuperior);
			xml += "<actividadSuperiorId>" + idActSupStr
					+ "</actividadSuperiorId>";
		}
		if (descripcion.length() > 0) {
			xml += "<descripcion>" + descripcion + "</descripcion>";
		}
		if (fechaInicio >= 0) {
			String fehcaIniStr = String.valueOf(fechaInicio);
			xml += "<fechaInicio>" + fehcaIniStr + "</fechaInicio>";
		}
		if (fechaFin >= 0) {
			String fehcaFinStr = String.valueOf(fechaFin);
			xml += "<fechaFin>" + fehcaFinStr + "</fechaFin>";
		}
		return xml;
	}

	protected void descerializar(Document doc) throws RemoteException {
		NodeList nodes = doc.getElementsByTagName("Actividad");
		if (nodes.getLength() != 1) {
			throw new RemoteException(
					"Debe haber solamente un nodo Actividad");
		}
		Element element = (Element) nodes.item(0);
		/*
		 * Como un nodo Actividad puede poseer varios nodos de atributos "id"
		 * (los adicionales pueden ser de usuarios o grupos), se lee el atributo
		 * "actividadId" y en caso de que no exista, se supone que el XML no
		 * provino de Integración, por lo que se lee el campo "id" para mantener
		 * la compatibilidad con las capas superiores.
		 */
		boolean encontroActividadId = false;
		String idStr = ParserXml.getValue("actividadId", element);
		if (idStr.length() > 0) {
			id = Long.valueOf(idStr);
			encontroActividadId = true;
		}
		if (!encontroActividadId) {
			idStr = ParserXml.getValue("id", element);
			if (idStr.length() > 0) {
				id = Long.valueOf(idStr);
			}
		}
		String idAmbSupStr = ParserXml.getValue("ambitoSuperiorId", element);
		if(idAmbSupStr.length() > 0)
			idAmbitoSuperior = Long.valueOf(idAmbSupStr);
		String idActSupStr = ParserXml.getValue("actividadSuperiorId", element);
		if(idActSupStr.length() > 0)
			idActividadSuperior = Long.valueOf(idActSupStr);
		nombre = ParserXml.getValue("nombre", element);
		tipo = ParserXml.getValue("tipo", element);
		descripcion = ParserXml.getValue("descripcion", element);
		String fechaInicioStr = ParserXml.getValue("fechaInicio", element);
		if(fechaInicioStr.length() > 0)
			fechaInicio = Long.valueOf(fechaInicioStr);
		String fechaFinStr = ParserXml.getValue("fechaFin", element);
		if(fechaFinStr.length() > 0)
			fechaFin = Long.valueOf(fechaFinStr);
	}

	protected void actualizar(Actividad actividadConDatosNuevos)
			throws RemoteException {
		if (actividadConDatosNuevos.getNombre().length() > 0) {
			setNombre(actividadConDatosNuevos.getNombre());
		}
		if (actividadConDatosNuevos.getDescripcion().length() > 0) {
			setDescripcion(actividadConDatosNuevos.getDescripcion());
		}
		if (actividadConDatosNuevos.getFechaInicio() > 0) {
			if (actividadConDatosNuevos.getFechaFin() > 0) {
				try {
					setFecha(actividadConDatosNuevos.getFechaInicio(),
							actividadConDatosNuevos.getFechaFin());
				} catch (RemoteException e) {
					String mensaje = "La fecha de inicio no puede ser posterior "
							+ "a la fecha de fin.";
					throw new RemoteException(mensaje);
				}
			} else {
				setFecha(actividadConDatosNuevos.getFechaInicio());
			}
		}
	}

	protected static String procesarConsultaIndividualIntegracion(String xmlMensaje)
			throws RemoteException {
		Document doc = ParserXml.getDocumentElement(xmlMensaje);
		NodeList nodes = doc.getElementsByTagName("Actividad");
		int cantidadActividades = nodes.getLength();
		switch (cantidadActividades) {
		case 0:
			ParserXml.procesarNotificacionIntegracion(className, xmlMensaje);
			break;
		case 1:
			return xmlMensaje;
		default:
			throw new RemoteException("Integracion no devolvio un unico nodo Actividad");
		}
		return xmlMensaje;
	}

	/* METODOS PUBLICOS DE TESTING */

	public String toString() {
		return "ID: " + id + "\n" + "ID AMBITO SUP: " + idAmbitoSuperior + "\n"
				+ "NOMBRE: " + nombre + "\n" + "FECHA INI: " + fechaInicio
				+ "\n" + "FECHA FIN: " + fechaFin + "\n" + "DESCRIPCION: "
				+ descripcion + "\n";
	}
}
