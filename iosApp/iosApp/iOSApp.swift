import SwiftUI
import Shared

@main
struct iOSApp: App {

    init() {
        KoinInit.shared.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}