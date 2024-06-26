package com.microservices.apis.controller;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.apis.model.UserReport;
import com.microservices.apis.model.UserChart;
import com.microservices.apis.repository.TelefoneRepository;
import com.microservices.apis.service.ImplementacaoUserDetailsService;
import com.microservices.apis.service.RelatorioService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.microservices.apis.model.Usuario;
import com.microservices.apis.repository.UsuarioRepository; 

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class IndexController {

	@Autowired
	private  UsuarioRepository usuarioRepository;
	@Autowired
	private  TelefoneRepository telefoneRepository;
	@Autowired
	private  RelatorioService relatorioService;
	@Autowired
	private  ImplementacaoUserDetailsService implementacaoUserDetailsService;
	@Autowired
	private JdbcTemplate jdbcTemplate;



	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> postCadastro(@RequestBody @Valid Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
		return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarioGet() {

		List<Usuario> usuario = (List<Usuario>) usuarioRepository.findAll();
		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(usuario, HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarios() {

		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		Page<Usuario> list = usuarioRepository.findAll(page);

		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findAll();
		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarioPage(@PathVariable("page") int pagina) {

		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		Page<Usuario> list = usuarioRepository.findAll(page);

		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findAll();
		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/userByName/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> userByName(@PathVariable("nome") String nome) {

		PageRequest pageRequest = null;
		Page<Usuario> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty()
				|| nome.equalsIgnoreCase("undefined"))) {

			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findUserByName(nome);
		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(list, HttpStatus.OK);
	}


	@GetMapping(value = "/userByName/{nome}/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> userByNamePage(@PathVariable("nome") String nome,
														@PathVariable("page") int page) {

		PageRequest pageRequest = null;
		Page<Usuario> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty()
				|| nome.equalsIgnoreCase("undefined"))) {

			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findUserByName(nome);
		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@PathVariable(value = "id") Long id, @RequestBody Usuario usuario) {

		Usuario usuarioExistente = usuarioRepository.findById(id).orElse(null);
		if (usuarioExistente == null) {
			return ResponseEntity.notFound().build();
		}
		// Atualiza os dados do usuário existente com os dados do usuário recebido na requisição
		usuarioExistente.setLogin(usuario.getLogin());
		usuarioExistente.setNome(usuario.getNome());
		usuarioExistente.setDataNascimento(usuario.getDataNascimento());
		usuarioExistente.setCep(usuario.getCep());
		usuarioExistente.setSenha(usuario.getSenha());
		// Atualiza os telefones, assumindo que o método setTelefones esteja definido na classe Usuario
		if(usuario.getTelefones() != null) {
		usuarioExistente.setTelefones(usuario.getTelefones());
		}
		// Verifica se a senha foi alterada
		if (!usuarioExistente.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuarioExistente.setSenha(senhaCriptografada);
		}
		// Salva o usuário atualizado
		Usuario usuarioSalvo = usuarioRepository.save(usuarioExistente);
		return ResponseEntity.ok(usuarioSalvo);
	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<Usuario> delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/removePhone/{id}", produces = "application/text")
	public String removePhone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		return "ok";
	}

	@GetMapping(value = "/graphic", produces = "application/json")
    public ResponseEntity<UserChart> graphic(){
		UserChart userChart = new UserChart();

		List<String> resultado = jdbcTemplate.queryForList("SELECT array_agg(nome) FROM usuario where salario > 0 and nome <> ''union all SELECT cast(array_agg(salario) as character varying[]) FROM usuario where salario > 0 and nome <> ''", String.class);
		if(!resultado.isEmpty()){
			String nomes = resultado.get(0).replaceAll("\\{","").replaceAll("\\}", "").replaceAll("\"", "");
			String salario = resultado.get(1).replaceAll("\\{","").replaceAll("\\}", "");

			userChart.setNome(nomes);
			userChart.setSalario(salario);
		}
		return new ResponseEntity<>(userChart, HttpStatus.OK);
	}
}


