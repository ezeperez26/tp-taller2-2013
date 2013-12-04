package fiuba.taller.actividad;

import java.util.List;

import fiuba.taller.actividad.excepcion.XmlErroneoExcepcion;

public class ActividadGrupalEvaluable extends ActividadGrupal implements
		Evaluable {

	protected static final String TIPO_ACTIVIDAD_GRUPAL_EVALUABLE = TIPO_ACTIVIDAD_GRUPAL
			+ " Evaluable";

	public ActividadGrupalEvaluable() {
		super();
		tipo = TIPO_ACTIVIDAD_GRUPAL_EVALUABLE;
	}

	public void evaluar(long idEvaluado, String nota) {
		// TODO Implementar
	}

	public String getNota(long idParticipante) {
		// TODO Implementar
		return null;
	}

	public List<Nota> getNotas() {
		// TODO Implementar
		return null;
	}

	public static boolean esTipoValido(String xml) {
		Actividad actividad = new Actividad();
		try {
			actividad.descerializar(xml);
		} catch (XmlErroneoExcepcion e) {
			return false;
		}
		if (actividad.tipo.equals(TIPO_ACTIVIDAD_GRUPAL_EVALUABLE)) {
			return true;
		}
		return false;
	}

	public static ActividadGrupalEvaluable crearInstancia(String xmlPropiedades)
			throws XmlErroneoExcepcion {
		ActividadGrupalEvaluable actividad = new ActividadGrupalEvaluable();
		actividad.descerializar(xmlPropiedades);
		// TODO(Pampa) Obtener un ID nuevo
		// actividad.id = nuevoId;
		actividad.tipo = TIPO_ACTIVIDAD_GRUPAL_EVALUABLE;
		// TODO(Pampa) Validar fecha y lanzar excepcion
		actividad.guardarEstado();
		return actividad;
	}

	public static ActividadGrupalEvaluable getActividad(long idActividad)
			throws XmlErroneoExcepcion {
		/*
		 * FIXME Si no existe la actividad con el ID especificado, se debe
		 * lanzar la excepcion ActividadInexistenteExcepcion
		 */
		ActividadGrupalEvaluable actividad = new ActividadGrupalEvaluable();
		actividad.levantarEstado(idActividad);
		return actividad;
	}
}