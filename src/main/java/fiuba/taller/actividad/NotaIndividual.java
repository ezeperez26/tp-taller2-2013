package fiuba.taller.actividad;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ws.services.ActualizarDatos;
import com.ws.services.ActualizarDatosResponse;
import com.ws.services.GuardarDatos;
import com.ws.services.GuardarDatosResponse;
import com.ws.services.IntegracionStub;

public class NotaIndividual extends Nota {

	private String username;
	
	private final static String NODO_USERNAME = "username";

	/**
	 * Constructor utilizado para realizar testing.
	 */
	@Deprecated
	public NotaIndividual() {
		super(-1);
	}

	private NotaIndividual(long idActividad, String username) {
		super(idActividad);
		this.username = username;
	}

	public NotaIndividual(long idActividad, String username, String valorNota) {
		super(idActividad);
		this.username = username;
		this.valor = valorNota;
	}

	public NotaIndividual(long idActividad, String username, String valorNota,
			String observaciones) {
		super(idActividad);
		this.username = username;
		this.valor = valorNota;
		this.observaciones = observaciones;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String serializar() {
		String output = "<WS><Nota>"
				+ "<idActividad>" + idActividad + "</idActividad>"
				+ "<username>" + username + "</username>"
				+ "<valor>" + valor + "</valor>"
				+ "<observaciones>" + observaciones + "</observaciones>"
				+ "</Nota></WS>";
		return output;
	}

	@Override
	public void descerializar(String xml) throws RemoteException {

		Document doc = getDocumentElement(xml);

		NodeList nodes = doc.getElementsByTagName(NODO_NOTA);
		if (nodes.getLength() != 1) {
			String message = "La cantidad de nodos Nota no es unica.";
			throw new RemoteException(message);
		}

		Node node = nodes.item(0);
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			String message = "El nodo Nota no es de tipo Element";
			throw new RemoteException(message);
		}

		Element element = (Element) node;
		idActividad = Long.valueOf(getValue(NODO_ID_ACTIVIDAD, element));
		username = getValue(NODO_USERNAME, element);
		valor = getValue(NODO_VALOR, element);
		observaciones = getValue(NODO_OBSERVACIONES, element);
	}

	@Override
	public void guardarNuevoEstado() throws RemoteException {
		IntegracionStub servicio = new IntegracionStub();
		GuardarDatos envio = new GuardarDatos();
		String xml = serializar();
		envio.setXml(xml);
//		System.out.println(xml);
		GuardarDatosResponse response = servicio.guardarDatos(envio);
		String retorno = response.get_return();
		System.out.println(retorno);
		procesarNotificacionIntegracion(retorno);
	}

	@Override
	public void actualizarEstado() throws RemoteException {
		if (!notaCreada(idActividad, username)) {
			guardarNuevoEstado();
		} else {
			IntegracionStub servicio = new IntegracionStub();
			ActualizarDatos envio = new ActualizarDatos();
			String xml = serializar();
			envio.setXml(xml);
			ActualizarDatosResponse respuesta = servicio.actualizarDatos(envio);
			String retorno = respuesta.get_return();
			System.out.println(retorno);
			procesarNotificacionIntegracion(retorno);
		}
	}

	@Override
	public void eliminarEstado() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String realizarConsulta() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Elimina la NotaIndividual de la base de datos.
	 * 
	 * @param idActividad
	 *            Identificador de la actividad individual evaluable.
	 * @param username
	 *            Identificador del participante.
	 */
	public static void eliminarNota(long idActividad, String username) {
		// TODO
	}

	/**
	 * Carga desde la base de datos la NotaIndividual.
	 * 
	 * @param idActividad
	 *            Identificador de la actividad individual evaluable.
	 * @param username
	 *            Identificador del participante.
	 * @return NotaIndividual cargada e instanciada.
	 * @throws NotaInexistenteExcepcion
	 *             Si no hay cargada una nota individual asociada a la actividad
	 *             y participante.
	 */
	public static NotaIndividual getNota(long idActividad, String username)
			throws RemoteException {
		// TODO Corregir
		NotaIndividual nota = new NotaIndividual(idActividad, username);
		return nota;
	}

	private static boolean notaCreada(long idActividad, String username) {
		try {
			getNota(idActividad, username);
		} catch (RemoteException e) {
			return false;
		}
		return true;
	}
}