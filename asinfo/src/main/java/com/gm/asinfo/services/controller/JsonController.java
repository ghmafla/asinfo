package com.gm.asinfo.services.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")

public class JsonController {

	@PostMapping(path = "/generateJson")
	public ResponseEntity<String> generarJson(@RequestBody String requestBody) throws JsonProcessingException {
		// crear la lista de strings
		List<String> lista = new ArrayList<>();
		lista.add("/paginas/compras/procesos/facturaProveedor.xhtml");
		lista.add("/paginas/compras/procesos/recepcionProveedor.xhtml");
		lista.add("/paginas/compras/Reportes/reporteA.xhtml");
		lista.add("/paginas/compras/Reportes/reporteB.xhtml");
		lista.add("/paginas/nomina/configuracion/empleados/empleado.xhtml");

		List<MenuItem> menu = buildMenu(lista);

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(menu);

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private List<MenuItem> buildMenu(List<String> lista) {
		List<MenuItem> menu = new ArrayList<>();

		for (String ruta : lista) {
			String[] partes = ruta.split("/");

			MenuItem item = new MenuItem(partes[partes.length - 1]);

			MenuSubItem padre = null;

			List<MenuItem> subMenu = menu;
			for (int i = 1; i < partes.length - 1; i++) {
				final String label = partes[i];
				padre = (MenuSubItem) subMenu.stream().filter(menuItem -> menuItem.getLabel().equals(label)).findFirst()
						.orElse(null);
				if (padre == null) {
					padre = new MenuSubItem(label);
					subMenu.add(padre);
				}

				if (padre.getItems() == null) {
					padre.setItems(new ArrayList<>());
				}
				subMenu = padre.getItems();
			}

			if (padre == null) {
				menu.add(item);
			} else {
				padre.getItems().add(new MenuUrlItem(item.getLabel(), ruta));
			}
		}

		return menu;
	}

	// Clase Padre
	private static class MenuItem {
		private String label;

		// Constructor
		public MenuItem(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}
	// Clase Hija LIST
		private static class MenuSubItem extends MenuItem {
			private List<MenuItem> items;

			// Constructor
			public MenuSubItem(String label, List items) {
				super(label);
				this.items = items;
			}

			public MenuSubItem(String label) {
				super(label);
			}

			public List<MenuItem> getItems() {
				return items;
			}

			public void setItems(List<MenuItem> items) {
				this.items = items;
			}

		}
		
	// Clase Hija URL
	private static class MenuUrlItem extends MenuItem {
		private String routerLink;

		// Constructor
		public MenuUrlItem(String label, String routerLink) {
			super(label);
			this.routerLink = routerLink;
		}

		public String getRouterLink() {
			return routerLink;
		}

		public void setRouterLink(String routerLink) {
			this.routerLink = routerLink;
		}

	}

	
}
