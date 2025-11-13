function SistemaAlunos() {
    this.urlApi = 'http://localhost:8080/api';
    this.alunoSelecionado = null;

    var self = this;

    this.cadastrarNovoAluno = function() {
        var aluno = {};
        aluno.nome = document.getElementById('nome').value;
        aluno.email = document.getElementById('email').value;
        aluno.curso = document.getElementById('curso').value;

        fetch(self.urlApi + '/alunos/cadastrar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(aluno)
        })
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {
            var mensagem;
            if (resultado.sucesso) {
                mensagem = 'Aluno cadastrado com sucesso!';
            } else {
                mensagem = 'Erro ao cadastrar aluno.';
            }

            self.exibirMensagem('mensagemCadastro', resultado.sucesso, mensagem);

            if (resultado.sucesso) {
                document.getElementById('formCadastro').reset();
            }
        })
        .catch(function(erro) {
            console.log('Erro:', erro);
            self.exibirMensagem('mensagemCadastro', false, 'Erro de conexão com o servidor.');
        });
    };

    this.buscarPorId = function() {
        var id = document.getElementById('idPesquisa').value;

        if (!id) {
            self.exibirMensagem('mensagemAtualizacao', false, 'Por favor, digite um ID válido.');
            return;
        }

        fetch(self.urlApi + '/alunos/buscar/' + id)
        .then(function(resposta) {
            if (resposta.ok) {
                return resposta.json();
            } else {
                throw new Error('Aluno não encontrado');
            }
        })
        .then(function(aluno) {
            if (aluno.erro) {
                self.exibirMensagem('mensagemAtualizacao', false, aluno.erro);
            } else {
                self.alunoSelecionado = aluno;
                self.preencherCamposAtualizacao(aluno);
                document.getElementById('formAtualizacao').classList.remove('escondido');
                self.exibirMensagem('mensagemAtualizacao', true, 'Aluno encontrado! Preencha os novos dados.');
            }
        })
        .catch(function(erro) {
            console.log('Erro:', erro);
            self.exibirMensagem('mensagemAtualizacao', false, 'Aluno não encontrado com este ID.');
        });
    };

    this.preencherCamposAtualizacao = function(aluno) {
        document.getElementById('nomeAtualizar').value = aluno.nome;
        document.getElementById('emailAtualizar').value = aluno.email;
        document.getElementById('cursoAtualizar').value = aluno.curso;
    };

    this.atualizarDados = function() {
        if (!self.alunoSelecionado) {
            self.exibirMensagem('mensagemAtualizacao', false, 'Nenhum aluno selecionado para atualização.');
            return;
        }

        var alunoAtualizado = {};
        alunoAtualizado.id = self.alunoSelecionado.id;
        alunoAtualizado.nome = document.getElementById('nomeAtualizar').value;
        alunoAtualizado.email = document.getElementById('emailAtualizar').value;
        alunoAtualizado.curso = document.getElementById('cursoAtualizar').value;

        fetch(self.urlApi + '/alunos/atualizar', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(alunoAtualizado)
        })
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {
            if (resultado.sucesso) {
                self.exibirMensagem('mensagemAtualizacao', true, 'Aluno atualizado com sucesso!');
                self.limparCampos();
                self.alunoSelecionado = null;
            } else {
                self.exibirMensagem('mensagemAtualizacao', false, 'Erro ao atualizar aluno.');
            }
        })
        .catch(function(erro) {
            console.log('Erro:', erro);
            self.exibirMensagem('mensagemAtualizacao', false, 'Erro de conexão com o servidor.');
        });
    };

    this.limparCampos = function() {
        document.getElementById('idPesquisa').value = '';
        document.getElementById('formAtualizacao').classList.add('escondido');
        document.getElementById('formAtualizacao').reset();
        self.alunoSelecionado = null;
    };

    this.carregarLista = function() {
        var carregando = document.getElementById('loading');
        var tabela = document.getElementById('tabelaAlunos');

        if (carregando && tabela) {
            carregando.classList.remove('escondido');
            tabela.classList.add('escondido');
        }

        fetch(self.urlApi + '/alunos')
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(alunos) {
            if (tabela) {
                self.mostrarAlunos(alunos);
                carregando.classList.add('escondido');
                tabela.classList.remove('escondido');
            }
        })
        .catch(function(erro) {
            console.log('Erro:', erro);
            self.exibirMensagem('mensagemLista', false, 'Erro ao carregar lista de alunos.');
            if (carregando) {
                carregando.classList.add('escondido');
            }
        });
    };

    this.mostrarAlunos = function(alunos) {
        var corpoTabela = document.getElementById('corpoTabela');

        if (!corpoTabela) {
            return;
        }

        if (alunos.length == 0) {
            corpoTabela.innerHTML = '<tr><td colspan="5" style="text-align: center;">Nenhum aluno cadastrado</td></tr>';
            return;
        }

        var linhas = '';
        for (var i = 0; i < alunos.length; i++) {
            var aluno = alunos[i];
            linhas = linhas + '<tr>' +
                '<td>' + aluno.id + '</td>' +
                '<td>' + aluno.nome + '</td>' +
                '<td>' + aluno.email + '</td>' +
                '<td>' + aluno.curso + '</td>' +
                '<td><button class="botao botao-vermelho" onclick="sistema.removerAluno(' + aluno.id + ')">Excluir</button></td>' +
                '</tr>';
        }

        corpoTabela.innerHTML = linhas;
    };

    this.removerAluno = function(id) {
        if (confirm('Tem certeza que deseja excluir este aluno?')) {
            fetch(self.urlApi + '/alunos/excluir/' + id, {
                method: 'DELETE'
            })
            .then(function(resposta) {
                return resposta.json();
            })
            .then(function(resultado) {
                if (resultado.sucesso == true) {
                    self.exibirMensagem('mensagemLista', true, 'Aluno excluído com sucesso!');
                    self.carregarLista();
                } else {
                    self.exibirMensagem('mensagemLista', false, 'Erro ao excluir aluno.');
                }
            })
            .catch(function(erro) {
                console.log('Erro:', erro);
                self.exibirMensagem('mensagemLista', false, 'Erro de conexão com o servidor.');
            });
        }
    };

    this.exibirMensagem = function(idElemento, sucesso, mensagem) {
        var elemento = document.getElementById(idElemento);
        if (elemento) {
            elemento.textContent = mensagem;

            if (sucesso == true) {
                elemento.className = 'mensagem mensagem-sucesso';
            } else {
                elemento.className = 'mensagem mensagem-erro';
            }

            elemento.classList.remove('escondido');

            setTimeout(function() {
                elemento.classList.add('escondido');
            }, 5000);
        }
    };
}
