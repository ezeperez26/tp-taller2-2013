package fiuba.taller.actividad;

import java.rmi.RemoteException;

public interface Serializable {

	/**
	 * Serializa el objeto en su estado actual.
	 * 
	 * @return String XML con el objeto serializado.
	 */
	public String serializar();

	/**
	 * Carga el estado del objeto a partir del XML.
	 * 
	 * @param xml
	 *            XML en un String que contiene el estado del objeto.
	 * @throws XmlErroneoExcepcion
	 *             cuando el XML es erroneo.
	 */
	public void descerializar(String xml) throws RemoteException;

	/**
	 * Guarda el estado actual del objeto a la base de datos.
	 * 
	 * @throws RemoteException
	 */
	public void guardarNuevoEstado() throws RemoteException;

	/**
	 * Actualiza el estado del objeto en la base de datos.
	 * 
	 * @throws RemoteException
	 */
	public void actualizarEstado() throws RemoteException;

	/**
	 * elimina el objeto de la base de datos.
	 * 
	 * @throws RemoteException
	 */
	public void eliminarEstado() throws RemoteException;

	/**
	 * A partir de los valores de los atributos internos de la instancia, genera
	 * un XML que es enviado a Integracion. Integracion devuelve un XML con
	 * todos los datos y este es devuelto.
	 * 
	 * @throws RemoteException
	 */
	public String realizarConsulta() throws RemoteException;
}
