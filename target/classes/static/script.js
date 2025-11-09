let currentMode = null;
let currentGameId = null;
let currentPlayerId = null;

function selectMode(mode) {
    currentMode = mode;
    document.getElementById('menu').style.display = 'none';
    document.getElementById('game').style.display = 'block';
    document.getElementById('mode-title').textContent = mode === 'PVC' ? 'Player vs Computer' : 'Player vs Player';
    document.getElementById('player-setup').style.display = 'block';
    document.getElementById('game-play').style.display = 'none';
}

function startGame() {
    const playerName = document.getElementById('player-name').value.trim();
    if (!playerName) {
        alert('Please enter your name');
        return;
    }

    fetch('/api/game/start', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            playerName: playerName,
            mode: currentMode
        })
    })
    .then(response => response.json())
    .then(data => {
        currentGameId = data.gameId;
        currentPlayerId = data.playerId;
        document.getElementById('player-setup').style.display = 'none';
        document.getElementById('game-play').style.display = 'block';
        document.getElementById('game-title').textContent = `Game Started! Make your move.`;
        document.getElementById('result').textContent = '';
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error starting game');
    });
}

function makeMove(move) {
    fetch('/api/game/move', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            gameId: currentGameId,
            playerId: currentPlayerId,
            move: move
        })
    })
    .then(response => response.json())
    .then(data => {
        if (currentMode === 'PVC') {
            const result = data.result;
            const computerMove = data.computerMove;
            document.getElementById('result').textContent =
                `You chose ${move}. Computer chose ${computerMove}. Result: ${result}`;
        } else {
            // PVP - wait for other player
            document.getElementById('result').textContent = `You chose ${move}. Waiting for opponent...`;
            // In a real implementation, you'd poll for the result or use WebSockets
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error making move');
    });
}

function resetGame() {
    currentMode = null;
    currentGameId = null;
    currentPlayerId = null;
    document.getElementById('game').style.display = 'none';
    document.getElementById('menu').style.display = 'block';
    document.getElementById('player-name').value = '';
}

function showHistory() {
    document.getElementById('menu').style.display = 'none';
    document.getElementById('history').style.display = 'block';

    fetch('/api/game/history')
    .then(response => response.json())
    .then(data => {
        const historyList = document.getElementById('history-list');
        historyList.innerHTML = '';
        data.forEach(game => {
            const gameItem = document.createElement('div');
            gameItem.className = 'game-item';
            gameItem.innerHTML = `
                <p>Game ID: ${game.id}</p>
                <p>Mode: ${game.gameMode}</p>
                <p>Status: ${game.status}</p>
                <p>Winner: ${game.winnerId || 'Draw'}</p>
            `;
            historyList.appendChild(gameItem);
        });
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error loading history');
    });
}

function backToMenu() {
    document.getElementById('history').style.display = 'none';
    document.getElementById('menu').style.display = 'block';
}
