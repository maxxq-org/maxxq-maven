package org.maxxq.maven.dependency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScopeTest {

    @Test
    void transitiveScope() {
        assertEquals( Scope.COMPILE, Scope.COMPILE.transitiveScope( Scope.COMPILE )  );
        assertEquals( Scope.RUNTIME, Scope.COMPILE.transitiveScope( Scope.RUNTIME)  );
        assertEquals( Scope.TEST, Scope.COMPILE.transitiveScope( Scope.TEST)  );
        assertEquals( Scope.PROVIDED, Scope.COMPILE.transitiveScope( Scope.PROVIDED)  );
        assertEquals( Scope.COMPILE, Scope.COMPILE.transitiveScope( Scope.IMPORT)  );
        
        assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.TEST)  );
        assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.RUNTIME)  );
        assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.COMPILE)  );
        assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.PROVIDED)  );
        assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.IMPORT)  );
        
        assertEquals( Scope.TEST, Scope.RUNTIME.transitiveScope( Scope.TEST)  );
        assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.COMPILE)  );
        assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.RUNTIME)  );
        assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.PROVIDED)  );
        assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.IMPORT)  );
        
        assertEquals( Scope.TEST, Scope.PROVIDED.transitiveScope( Scope.TEST)  );
        assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.COMPILE)  );
        assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.RUNTIME)  );
        assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.PROVIDED)  );
        assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.IMPORT)  );
    }
    
}
