# Controle de Estacionamento - FUMEC

Este é um projeto desenvolvido para a disciplina de **LTP (Linguagem de Técnicas de Programação)** da FUMEC. Trata-se de um sistema de controle de estacionamento feito em Java que utiliza persistência em arquivos binários de acesso aleatório (`RandomAccessFile`), salvando os registros no arquivo `EST.DAT`.

---

## 🚀 Funcionalidades Integradas

O sistema unifica as contribuições de todos os membros do grupo em uma aplicação robusta com as seguintes opções:

1. **Entrada de Veículo**:
   - Geração automática e sequencial do código de estacionamento (`codEst`).
   - Validação de formato e consistência para todos os dados inseridos (Placa com 7 caracteres, hora no formato `HH:MM`, etc.).
   - Validação real de data (`DD/MM/AAAA`) utilizando a API moderna do Java (`java.time`).
   - Associação e validação de marca por meio de tabela de siglas (ex: ao digitar `VW`, o sistema preenche automaticamente `Volkswagen`).
   
2. **Saída de Veículo**:
   - Pesquisa pelo código do veículo.
   - Validação da hora de saída (que deve ser maior ou igual à hora de entrada).
   - Cálculo automático do valor a ser pago com base nas tarifas da categoria e no horário de entrada (tarifas diurnas para entradas até as 18:00 e tarifas noturnas para após as 18:00).
   - Atualização automática do registro de entrada para inativo (`N`) e gravação do novo registro do tipo Saída (`S`).

3. **Consultas de Veículos**:
   - Pesquisa de registro ativo por Código de Estacionamento.
   - Pesquisa de registros ativos por Placa do veículo.
   - Listagem em formato de tabela de todos os veículos atualmente estacionados (que ainda não registraram saída).
   - Listagem em formato de tabela do histórico completo de registros ativos (entradas e saídas).

4. **Exclusão de Veículo**:
   - Exclusão lógica (desativação) de qualquer registro de veículo no banco de dados.

5. **Relatório de Faturamento**:
   - Resumo de veículos atualmente estacionados.
   - Quantidade de saídas registradas.
   - Faturamento total acumulado (em R$).
   - Detalhamento de receita dividida por categoria de veículo (`GI` - Grande Importado, `PI` - Pequeno Importado, `GN` - Grande Nacional, `PN` - Pequeno Nacional).

---

## 🛠️ Como Executar o Projeto

Certifique-se de possuir o Java Development Kit (JDK) instalado no seu sistema.

1. **Compilar os arquivos Java**:
   Abra o terminal na pasta raiz do projeto e execute:
   ```bash
   javac Main.java Estacionamento.java
   ```

2. **Iniciar a aplicação**:
   Execute o comando:
   ```bash
   java Main
   ```

---

## 👥 Estrutura do Grupo e Contribuições

O desenvolvimento foi dividido entre os integrantes do grupo e unificado da seguinte forma:

* **Cachaça e João** ([Cachaça e João/](file:///c:/Users/tbals/OneDrive/Área de Trabalho/LTP/Projeto Final (Humano)/Cachaça%20e%20João)):
  * Implementação da estrutura de persistência do arquivo binário e métodos de gravação.
  * Validação de datas reais (`validarData`).
* **Sarah** ([Sarah/](file:///c:/Users/tbals/OneDrive/Área de Trabalho/LTP/Projeto Final (Humano)/Sarah)):
  * Criação da lógica de validação de marcas via códigos/siglas de duas letras (`pesquisarMarcaVeic`).
* **Thiago** ([Thiago/](file:///c:/Users/tbals/OneDrive/Área de Trabalho/LTP/Projeto Final (Humano)/Thiago)):
  * Desenvolvimento do fluxo de saída de veículos (`sairVeiculo`), cálculo de tarifas e validação de formato de horas (`validarHora`).
* **Richard** ([Richard/](file:///c:/Users/tbals/OneDrive/Área de Trabalho/LTP/Projeto Final (Humano)/Richard)):
  * Participação na estruturação e lógica do projeto.
