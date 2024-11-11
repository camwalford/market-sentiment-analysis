import os
import uvicorn
from app.main import app

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8082))  # Default port is 8000
    uvicorn.run(app, host="0.0.0.0", port=port)