package fiuba.taller.actividad;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fiuba.taller.actividad.Grupo;

public class GrupoTest {
	
	Grupo grupo;
	String xmlADescerializar;
	long idGrupo;
	String usernameParticipante1;
	String usernameParticipante2;
	
	@Before
	public void setUp() throws Exception {
		grupo = new Grupo();
		idGrupo = 10;
		usernameParticipante1 = "pepe";
		usernameParticipante2 = "tito";
		xmlADescerializar =  "<?xml version=\"1.0\"?><WS><Grupo>"
				+ "<IdGrupo>" + idGrupo + "</IdGrupo>"
				+ "<usernameParticipante>" + usernameParticipante1 + "</usernameParticipante>"
				+ "<usernameParticipante>" + usernameParticipante2 + "</usernameParticipante>"
				+ "</Grupo></WS>";
	}

	@After
	public void tearDown() throws Exception {
	}
/*
	@Test
	public void testGetXml() {
		fail("Not yet implemented");
	}
*/
	@Test
	public void serializar() {		
		grupo.descerializar(xmlADescerializar);
		
		String xmlFinal = grupo.serializar();
		
		assertEquals(xmlADescerializar, xmlFinal);
	}
	
	@Test
	public void testDescerializar() {
		
		grupo.descerializar(xmlADescerializar);
		
		assertEquals(idGrupo, grupo.getId());

		if(!grupo.getUsernameParticipantes().get(0).equals(usernameParticipante1)){
			fail("IdParticipante esperado: "+usernameParticipante1+" IdParticipante encontrado: "
					+ grupo.getUsernameParticipantes().get(0));
		}
		if(!grupo.getUsernameParticipantes().get(1).equals(usernameParticipante2)){
			fail("IdParticipante esperado: "+usernameParticipante2+" IdParticipante encontrado: "
					+ grupo.getUsernameParticipantes().get(1));
		}
	}
/*
	@Test
	public void testGuardarEstado() {
		fail("Not yet implemented");
	}
*/
}