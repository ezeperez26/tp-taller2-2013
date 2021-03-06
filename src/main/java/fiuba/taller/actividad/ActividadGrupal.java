package fiuba.taller.actividad;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ActividadGrupal extends Actividad {

	protected static final String TIPO_ACTIVIDAD_GRUPAL = "Grupal";
	protected boolean gruposExclusivos;
	protected List<Grupo> grupos;

	public ActividadGrupal() {
		super();
		tipo = TIPO_ACTIVIDAD_GRUPAL;
		gruposExclusivos = false;
		grupos = null;
	}

	public boolean esDeGruposExclusivos() {
		return gruposExclusivos;
	}

	public void setGruposExclusivos() {
		gruposExclusivos = true;
	}
	
	public Grupo getGrupo(long idGrupo) throws RemoteException {
		if (!gruposCargados()) {
			cargarGrupos();
		}
		for(Grupo grupo : grupos) {
			if (grupo.getId() == idGrupo) {
				return grupo;
			}
		}
		String mensaje = "No existe el grupo con el identificador " + idGrupo;
		throw new RemoteException(mensaje);
	}

	public List<Grupo> getGrupos() {
		if (!gruposCargados()) {
			cargarGrupos();
		}
		return grupos;
	}

	public void agregarGrupo(Grupo grupo) throws RemoteException {
		if (!gruposCargados()) {
			cargarGrupos();
		}
		if (gruposExclusivos) {
			verificarParticipantes(grupo);
		}
		grupo.setIdActividad(id);
		grupo.guardarNuevoEstado();
		grupos.add(grupo);
	}
	
	public void agregarParticipanteAGrupo(long idGrupo, 
			String usernameNuevoParticipante) throws RemoteException {
		Grupo grupo = this.getGrupo(idGrupo);
		grupo.agregarParticipante(usernameNuevoParticipante);
		if (this.gruposExclusivos) {
			try {
				this.verificarParticipantes(grupo);
			} catch (RemoteException e) {
				// Si el participante ya se encuentra en otro grupo, 
				// lo borro y lanzo excepcion
				grupo.eliminarParticipante(usernameNuevoParticipante);
				throw e;
			}
		}
	}

	public void eliminarGrupo(long idGrupo) throws RemoteException {
		if (!gruposCargados()) {
			cargarGrupos();
		}
		Grupo grupoAEliminar = getGrupo(idGrupo);
		grupos.remove(grupoAEliminar);
		/*
		 * FIXME(Jorge) Hará falta llamar a Integración para eliminar el
		 * grupo o con el método "GuardarEstado" alcanza?
		 */
	}
	
	public void eliminarParticipanteDeGrupo(long idGrupo, 
			String usernameParticipanteAEliminar) 
					throws RemoteException {
		Grupo grupo = getGrupo(idGrupo);
		grupo.eliminarParticipante(usernameParticipanteAEliminar);
		// Si ya no quedan participantes, elimino totalmente el grupo
		if (grupo.tamanio() == 0) {
			grupo.eliminarEstado();
			grupos.remove(grupo);
		}
	}

	/* METODOS DE CLASE (ESTATICOS) */

	public static boolean esTipoValido(String xml) {
		Actividad actividad = new Actividad();
		try {
			actividad.descerializar(xml);
		} catch (RemoteException e) {
			return false;
		}
		/*
		 * Si el campo "Tipo" comienza con la palabra "Grupal", se considerara
		 * de tipo valido. Es decir, ActividadGrupalEvaluable tambien se
		 * considera como ActividadGrupal (por ser clase derivada)
		 */
		if (actividad.tipo.startsWith(TIPO_ACTIVIDAD_GRUPAL)) {
			return true;
		}
		return false;
	}

	public static ActividadGrupal crearActividad(String xmlPropiedades)
			throws RemoteException {
		ActividadGrupal actividad = new ActividadGrupal();
		actividad.descerializar(xmlPropiedades);
		actividad.setId(-1);
		actividad.tipo = TIPO_ACTIVIDAD_GRUPAL;
		// TODO(Pampa) Validar fecha y lanzar excepcion
		actividad.guardarNuevoEstado();
		return actividad;
	}

	public static ActividadGrupal getActividad(long idActividad)
			throws RemoteException {
		/*
		 * FIXME Si no existe la actividad con el ID especificado, se debe
		 * lanzar la excepcion.
		 */
		String propiedades = getPropiedades(idActividad);
		if(!esTipoValido(propiedades)) {
			String mensaje = "La actividad a cargar no es Grupal";
			throw new RemoteException(mensaje);
		}
		ActividadGrupal actividad = new ActividadGrupal();
		actividad.descerializar(propiedades);
		return actividad;
	}

	/* METODOS PROTEGIDOS AUXILIARES */

	@Override
	protected String serializarInterno() {
		return super.serializarInterno() + "<gruposExclusivos>"
				+ gruposExclusivos + "</gruposExclusivos>";
	}

	@Override
	protected void descerializar(Document doc) throws RemoteException {
		super.descerializar(doc);
		NodeList nodes = doc.getElementsByTagName("Actividad");
		if (nodes.getLength() != 1) {
			throw new RemoteException("Debe haber solamente un nodo Actividad");
		}
		Node node = nodes.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			this.gruposExclusivos = Boolean.valueOf(ParserXml.getValue(
					"gruposExclusivos", element));
		}
	}

	/* METODOS PRIVADOS AUXILIARES */

	private boolean gruposCargados() {
		return grupos != null;
	}

	private void cargarGrupos() {
		grupos = new ArrayList<>();
		/*
		 * TODO(Jorge o Pampa?) Implementar. Se debe cargar desde la base de
		 * datos la lista de grupos.
		 */
	}

	private void verificarParticipantes(Grupo grupoNuevo)
			throws RemoteException {
		for (Grupo grupo : grupos) {
			if (grupoNuevo != grupo) {
				if (grupo.contieneParticipantesDe(grupo)) {
					List<String> participantesRepetidos = grupo
							.getParticipantesDuplicados(grupoNuevo);
					String mensaje = "El grupo a agregar tiene los siguientes "
							+ "participantes duplicados: ";
					for (String participante : participantesRepetidos) {
						mensaje += participante;
					}
					throw new RemoteException(mensaje);
				}
			}
		}
	}
}
