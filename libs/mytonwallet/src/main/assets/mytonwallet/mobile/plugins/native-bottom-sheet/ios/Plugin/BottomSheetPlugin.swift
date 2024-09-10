import Capacitor
import FloatingPanel
import UIKit
import WebKit

private let CORNER_RADIUS = 16.0
private let EASING_1 = CGPoint(x: 0.16, y: 1)
private let EASING_2 = CGPoint(x: 0.3, y: 1)
private let ANIMATION_DURATION = 0.600
private let HALF_FRACTIONAL_INSET = 0.5
private let MAX_HALF_INSET = 0.85
private let FULL_INSET = 8.0

@objc(BottomSheetPlugin)
public class BottomSheetPlugin: CAPPlugin, FloatingPanelControllerDelegate {
    let timingParameters = UICubicTimingParameters(controlPoint1: EASING_1, controlPoint2: EASING_2)
    let fpc = FloatingPanelController()

    var capVc: CAPBridgeViewControllerForBottomSheet?
    var isPrepared = false
    var currentDelegateCall: CAPPluginCall?
    public var currentOpenSelfCall: CAPPluginCall?
    var currentHalfY: CGFloat?
    var prevStatusBarStyle: UIStatusBarStyle?
    public var isHalfSize = false

    @objc func prepare(_ call: CAPPluginCall) {
        ensureLocalOrigin()

        isPrepared = true

        DispatchQueue.main.async { [self] in
            fpc.layout = MyPanelLayout()
            fpc.delegate = self
            fpc.isRemovalInteractionEnabled = false
            fpc.backdropView.dismissalTapGestureRecognizer.isEnabled = false
            fpc.surfaceView.appearance.cornerRadius = CORNER_RADIUS
            fpc.surfaceView.grabberHandle.isHidden = true

            capVc = CAPBridgeViewControllerForBottomSheet()
            fpc.set(contentViewController: capVc)
            fpc.track(scrollView: capVc!.webView!.scrollView)

            // Fix unexpected scrolling within WebView
            fpc.contentInsetAdjustmentBehavior = .never

            // Fix warning "Unable to simultaneously satisfy constraints."
            // https://github.com/scenee/FloatingPanel/issues/557
            fpc.invalidateLayout()

            let topVc = bridge!.viewController!
            topVc.view.clipsToBounds = true
            // Check if the view controller is already being presented
            if fpc.presentingViewController != nil {
                call.resolve()
            } else {
                // Present the view controller modally
                topVc.present(fpc, animated: false) {
                    call.resolve()
                }
            }
        }
    }

    @objc func disable(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegating()

        DispatchQueue.main.async { [self] in
            if let presentingVc = self.fpc.presentingViewController {
                let topBottomSheetPlugin = self.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin

                presentingVc.dismiss(animated: false) {
                    call.resolve()
                }
            } else {
                call.resolve()
            }
        }
    }

    @objc func enable(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegating()

        DispatchQueue.main.async { [self] in
            if (self.fpc.presentingViewController != nil) {
                call.resolve()
                return
            }

            if let presentedVc = self.bridge!.viewController!.presentedViewController {
                presentedVc.dismiss(animated: false) {
                    self.bridge?.viewController?.present(self.fpc, animated: false) {
                        call.resolve()
                    }
                }
            } else {
                self.bridge?.viewController?.present(self.fpc, animated: false) {
                    call.resolve()
                }
            }
        }
    }

    @objc func applyScrollPatch(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [self] in
            guard let topVc = bridge?.viewController?.parent?.presentingViewController as? CAPBridgeViewController else {
                call.resolve()
                return
            }
            let topBottomSheetPlugin = topVc.bridge?.plugin(withName: "BottomSheet") as? BottomSheetPlugin
            call.resolve()
        }
    }

    @objc func clearScrollPatch(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [self] in
            let topVc = bridge!.viewController!.parent!.presentingViewController as! CAPBridgeViewController
            let topBottomSheetPlugin = topVc.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin
            call.resolve()
        }
    }

    @objc func delegate(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegating()

        resolveOpenCalls()
        currentDelegateCall = call

        DispatchQueue.main.async { [self] in
            capVc!.bridge?.plugin(withName: "BottomSheet")!.notifyListeners("delegate", data: [
                "key": call.getString("key")!,
                "globalJson": call.getString("globalJson")!
            ])

            let screenHeight = bridge!.viewController!.view!.superview!.frame.height
            currentHalfY = screenHeight - screenHeight * HALF_FRACTIONAL_INSET
        }
    }

    @objc func release(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegating()

        DispatchQueue.main.async { [self] in
            let releaseKey = call.getString("key")
            if releaseKey == currentDelegateCall?.getString("key") || releaseKey == "*" {
                doClose()
            }

            call.resolve()
        }
    }

    @objc func openSelf(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegated()

        currentOpenSelfCall = call

        DispatchQueue.main.async { [self] in
            let parentFpc = bridge!.viewController!.parent! as! FloatingPanelController
            parentFpc.surfaceView.backgroundColor = UIColor(hexString: call.getString("backgroundColor")!)

            let topVc = parentFpc.presentingViewController as! CAPBridgeViewController
            let topBottomSheetPlugin = topVc.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin

            topBottomSheetPlugin.doOpen(
                height: CGFloat(Float(call.getString("height")!)!)
            )

            bridge!.webView!.becomeFirstResponder()
        }
    }

    @objc func closeSelf(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegated()

        call.resolve()

        DispatchQueue.main.async { [self] in
            if currentOpenSelfCall == nil || currentOpenSelfCall!.getString("key") != call.getString("key") {
                return
            }

            let topVc = bridge!.viewController!.parent!.presentingViewController as! CAPBridgeViewController
            let topBottomSheetPlugin = topVc.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin
            topBottomSheetPlugin.doClose()
        }
    }

    @objc func toggleSelfFullSize(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegated()

        call.resolve()

        DispatchQueue.main.async { [self] in
            if currentOpenSelfCall == nil {
                return
            }

            let topVc = self.bridge!.viewController!.parent!.presentingViewController as! CAPBridgeViewController
            let topBottomSheetPlugin = topVc.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin

            if !topBottomSheetPlugin.isHalfSize {
                return
            }

            let isFullSize = call.getBool("isFullSize") == true
            let layout = topBottomSheetPlugin.fpc.layout as! MyPanelLayout

            if isFullSize && layout.anchors[.full] == nil {
                layout.anchors[.full] = layout.fullAnchor
            }

            topBottomSheetPlugin.animateTo(to: isFullSize ? .full : .half)
        }
    }


    @objc func openInMain(_ call: CAPPluginCall) {
        ensureLocalOrigin()
        ensureDelegated()

        call.resolve()

        DispatchQueue.main.async { [self] in
            let topVc = bridge!.viewController!.parent!.presentingViewController as! CAPBridgeViewController
            let topBottomSheetPlugin = topVc.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin
            topBottomSheetPlugin.notifyListeners("openInMain", data: [
                "key": call.getString("key")!
            ])
        }
    }

    // Extra security level, potentially redundant
    private func ensureLocalOrigin() {
        DispatchQueue.main.sync { [self] in
            precondition(bridge!.webView!.url!.absoluteString.hasPrefix(bridge!.config.serverURL.absoluteString))
        }
    }

    private func ensureDelegating() {
        precondition(isPrepared)
    }

    private func ensureDelegated() {
        precondition(!isPrepared)
    }

    public func doOpen(height: CGFloat) {
        let screenHeight = bridge!.viewController!.view!.superview!.frame.height
        let newFractionalInset = min(height / screenHeight, MAX_HALF_INSET)
        let layout = fpc.layout as! MyPanelLayout

        if newFractionalInset < MAX_HALF_INSET {
            layout.anchors[.half] = FloatingPanelLayoutAnchor(fractionalInset: newFractionalInset, edge: .bottom, referenceGuide: .superview)
            layout.anchors[.full] = nil
            currentHalfY = screenHeight - screenHeight * newFractionalInset
            animateTo(to: .half)
            isHalfSize = true
        } else {
            layout.anchors[.half] = nil
            layout.anchors[.full] = layout.fullAnchor
            currentHalfY = screenHeight - screenHeight * HALF_FRACTIONAL_INSET
            animateTo(to: .full)
            isHalfSize = false
        }
    }

    public func doClose() {
        resolveOpenCalls()

        if fpc.state == .hidden {
            return
        }

        isHalfSize = false
        animateTo(to: .hidden)
    }

    private func resolveOpenCalls() {
        currentDelegateCall?.resolve()
        currentDelegateCall = nil

        let childBottomSheetPlugin = capVc!.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin
        childBottomSheetPlugin.currentOpenSelfCall?.resolve()
        childBottomSheetPlugin.currentOpenSelfCall = nil
    }

    private func animateTo(to: FloatingPanelState) {
        if to == .half && fpc.layout.anchors[.half] == nil {
            return
        }

        let timing = UICubicTimingParameters(controlPoint1: EASING_1, controlPoint2: EASING_2)
        let animator = UIViewPropertyAnimator(duration: ANIMATION_DURATION, timingParameters: timing)
        fpc.isAnimating = true

        animator.addAnimations { [self] in
            fpc.move(to: to, animated: false)
        }

        animator.addCompletion { [weak self] _ in
            self?.fpc.isAnimating = false
        }
        animator.startAnimation()
    }

    public func floatingPanelDidMove(_ fpc: FloatingPanelController) {
        if currentHalfY == nil {
            return
        }

        let view = bridge!.viewController!.view!
        let y = fpc.surfaceView.frame.origin.y
        let offsetTop = view.safeAreaInsets.top + FULL_INSET
        let currentOffsetY = y - offsetTop
        let currentHalfOffsetY = currentHalfY! - offsetTop
        let progress = 1 - currentOffsetY / currentHalfOffsetY

        let maxMainHeight = view.superview!.frame.height
        let minMainHeight = maxMainHeight - (view.safeAreaInsets.top * 2)
        let maxScaleFactor = 1 - minMainHeight / maxMainHeight

        let scale = 1 - maxScaleFactor * max(progress, 0)

        view.transform = CGAffineTransform(scaleX: scale, y: scale)

        let topVc = bridge!.viewController!
        topVc.view.layer.cornerRadius = scale < 1 ? CORNER_RADIUS : 0.0

        let childBottomSheetPlugin = capVc!.bridge!.plugin(withName: "BottomSheet") as! BottomSheetPlugin
        childBottomSheetPlugin.notifyListeners("move", data: nil)
    }

    public func floatingPanelDidChangeState(_ fpc: FloatingPanelController) {
        let inEnteringFull = fpc.state == .full && prevStatusBarStyle == nil
        let isLeavingFull = fpc.state != .full && prevStatusBarStyle != nil

        if inEnteringFull {
            prevStatusBarStyle = bridge!.statusBarStyle
            bridge!.statusBarStyle = .lightContent
        } else if isLeavingFull {
            bridge!.statusBarStyle = prevStatusBarStyle!
            prevStatusBarStyle = nil
        }

        if fpc.state == .hidden {
            resolveOpenCalls()
            bridge!.webView!.becomeFirstResponder()
        }
    }
}

class CAPBridgeViewControllerForBottomSheet: CAPBridgeViewController {
    override func instanceDescriptor() -> InstanceDescriptor {
        let descriptor = super.instanceDescriptor()
        descriptor.serverURL = String(format: "%@://%@/?bottom-sheet", descriptor.urlScheme!, descriptor.urlHostname!)
        return descriptor
    }
}

class MyPanelLayout: FloatingPanelLayout {
    let position: FloatingPanelPosition = .bottom
    let initialState: FloatingPanelState = .hidden
    var anchors: [FloatingPanelState: FloatingPanelLayoutAnchoring] = [
        .half: FloatingPanelLayoutAnchor(fractionalInset: HALF_FRACTIONAL_INSET, edge: .bottom, referenceGuide: .superview),
        .hidden: FloatingPanelLayoutAnchor(fractionalInset: 0, edge: .bottom, referenceGuide: .superview)
    ]
    let fullAnchor = FloatingPanelLayoutAnchor(absoluteInset: FULL_INSET, edge: .top, referenceGuide: .safeArea)

    func backdropAlpha(for state: FloatingPanelState) -> CGFloat {
        switch state {
        case .full: return 0.45
        case .half: return 0.35
        default: return 0.0
        }
    }
}

extension UIColor {
    convenience init(hexString: String) {
        let hex = hexString.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int = UInt64()
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
    }
}
